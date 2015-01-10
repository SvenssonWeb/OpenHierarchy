/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.sections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.beans.MenuItem;
import se.unlogic.hierarchy.core.beans.SectionMenu;
import se.unlogic.hierarchy.core.beans.SectionMenuItem;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.cache.BackgroundModuleCache;
import se.unlogic.hierarchy.core.cache.BackgroundModuleXSLTCache;
import se.unlogic.hierarchy.core.cache.ForegroundModuleCache;
import se.unlogic.hierarchy.core.cache.ForegroundModuleXSLTCache;
import se.unlogic.hierarchy.core.cache.MenuItemCache;
import se.unlogic.hierarchy.core.cache.SectionCache;
import se.unlogic.hierarchy.core.enums.HTTPProtocol;
import se.unlogic.hierarchy.core.enums.ResponseType;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.ForegroundNullResponseException;
import se.unlogic.hierarchy.core.exceptions.ProtocolRedirectException;
import se.unlogic.hierarchy.core.exceptions.RequestException;
import se.unlogic.hierarchy.core.exceptions.SectionDefaultURINotFoundException;
import se.unlogic.hierarchy.core.exceptions.SectionDefaultURINotSetException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.exceptions.UnhandledModuleException;
import se.unlogic.hierarchy.core.interfaces.BackgroundModule;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.ForegroundModule;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.FullSectionInterface;
import se.unlogic.hierarchy.core.interfaces.FullSystemInterface;
import se.unlogic.hierarchy.core.interfaces.RootSectionInterface;
import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.http.URIParser;

public class Section implements RootSectionInterface, FullSectionInterface {

	private static HashMap<Integer, SectionInterface> sectionInterfaceMap = new HashMap<Integer, SectionInterface>();
	private static final ReentrantReadWriteLock mapLock = new ReentrantReadWriteLock();
	private static final Lock mapReadLock = mapLock.readLock();
	private static final Lock mapWriteLock = mapLock.writeLock();

	protected Logger log = Logger.getLogger(this.getClass());

	private ForegroundModuleCache foregroundModuleCache;
	private BackgroundModuleCache backgroundModuleCache;
	private MenuItemCache menuCache;
	private ForegroundModuleXSLTCache foregroundModuleXSLTCache;
	private BackgroundModuleXSLTCache backgroundModuleXSLTCache;
	private SectionCache sectionCache;
	private FullSystemInterface systemInterface;
	private SectionDescriptor sectionDescriptor;
	private SectionInterface parentSectionInterface;

	protected final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	protected final Lock r = rwl.readLock();
	protected final Lock w = rwl.writeLock();

	private void addSectionInterface() {

		mapWriteLock.lock();
		try {
			sectionInterfaceMap.put(this.sectionDescriptor.getSectionID(), this);
		} finally {
			mapWriteLock.unlock();
		}
	}

	private void removeSectionInterface() {

		mapWriteLock.lock();
		try {
			sectionInterfaceMap.remove(this.sectionDescriptor.getSectionID());
		} finally {
			mapWriteLock.unlock();
		}
	}

	public static SectionInterface getSectionInterface(Integer sectionID) {

		mapReadLock.lock();
		try {
			return sectionInterfaceMap.get(sectionID);
		} finally {
			mapReadLock.unlock();
		}
	}

	public Section(SectionDescriptor sectionDescriptor, SectionInterface parentSectionInterface, FullSystemInterface systemInterface) {

		w.lock();
		try {
			log.info("Section " + sectionDescriptor + " starting...");

			this.sectionDescriptor = sectionDescriptor;

			if (parentSectionInterface == null) {

				systemInterface.setRootSection(this);

			} else {

				this.parentSectionInterface = parentSectionInterface;
			}

			this.systemInterface = systemInterface;

			menuCache = new MenuItemCache(systemInterface.getCoreDaoFactory(), sectionDescriptor);
			foregroundModuleXSLTCache = new ForegroundModuleXSLTCache(systemInterface.getApplicationFileSystemPath());
			foregroundModuleCache = new ForegroundModuleCache(this);
			foregroundModuleCache.addCacheListener(menuCache);
			foregroundModuleCache.addCacheListener(foregroundModuleXSLTCache);
			foregroundModuleCache.addCacheListener(systemInterface.getGlobalForegroundModuleCacheListener());

			backgroundModuleXSLTCache = new BackgroundModuleXSLTCache(systemInterface.getApplicationFileSystemPath());
			backgroundModuleCache = new BackgroundModuleCache(this);
			backgroundModuleCache.addCacheListener(backgroundModuleXSLTCache);
			backgroundModuleCache.addCacheListener(systemInterface.getGlobalBackgroundModuleCacheListener());

			sectionCache = new SectionCache(this);
			sectionCache.addCacheListener(menuCache);
			sectionCache.addCacheListener(systemInterface.getGlobalSectionCacheListener());

			log.info("Caching modules...");

			try {
				this.foregroundModuleCache.cacheModules(false);
				this.backgroundModuleCache.cacheModules(false);
			} catch (Exception e) {
				log.error("Error caching modules for section " + this.sectionDescriptor, e);
			}

			log.info("Caching subsections...");

			try {
				this.sectionCache.cacheSections();
			} catch (Exception e) {
				log.error("Error caching subsections for section " + this.sectionDescriptor, e);
			}

			this.addSectionInterface();

			log.info("Section " + this.sectionDescriptor + " started");
		} finally {
			w.unlock();
		}
	}

	public void update(SectionDescriptor sectionDescriptor) {

		w.lock();
		try {
			this.sectionDescriptor = sectionDescriptor;
			this.menuCache.setSectionDescriptor(sectionDescriptor);

			//TODO reload the menu cache of this section all subsections

		} finally {
			w.unlock();
		}
	}

	public void unload() {

		w.lock();
		try {
			log.info("Unloading section " + this.sectionDescriptor);

			this.foregroundModuleCache.unload();
			this.backgroundModuleCache.unload();
			this.sectionCache.unload();

			this.removeSectionInterface();

			log.info("Section " + this.sectionDescriptor + " unloaded");
		} finally {
			w.unlock();
		}
	}

	public ForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, HTTPProtocol enforcedHTTPProtocol) throws RequestException {

		r.lock();

		if (this.sectionDescriptor.getRequiredProtocol() != null) {

			enforcedHTTPProtocol = this.sectionDescriptor.getRequiredProtocol();
		}

		boolean isDefaultURI = false;

		try {
			if (uriParser.size() == 0) {
				if (user == null) {

					if(StringUtils.isEmpty(sectionDescriptor.getAnonymousDefaultURI())){

						throw new SectionDefaultURINotSetException(sectionDescriptor, false);

					}

					uriParser.addToURI(sectionDescriptor.getAnonymousDefaultURI());
					isDefaultURI = true;

				} else {

					if(StringUtils.isEmpty(sectionDescriptor.getUserDefaultURI())){

						throw new SectionDefaultURINotSetException(sectionDescriptor, true);
					}

					uriParser.addToURI(sectionDescriptor.getUserDefaultURI());
					isDefaultURI = true;
				}
			}

			Entry<SectionDescriptor, Section> sectionCacheEntry;

			if (uriParser.size() >= 1 && (sectionCacheEntry = this.sectionCache.getEntry(uriParser.get(0))) != null) {

				if (AccessUtils.checkAccess(user, sectionCacheEntry.getKey())) {

					ForegroundModuleResponse moduleResponse = sectionCacheEntry.getValue().processRequest(req, res, user, uriParser.getNextLevel(), enforcedHTTPProtocol);

					if (moduleResponse != null && !res.isCommitted()) {

						// Check if the user has changed
						if (moduleResponse.isUserChanged()) {
							user = (User) req.getSession(true).getAttribute("user");
						}

						moduleResponse.setMenu(this.menuCache.getUserMenu(user,moduleResponse.getMenu(),uriParser));

						if (this.sectionDescriptor.hasBreadCrumb() && !moduleResponse.isExcludeSectionBreadcrumbs()) {
							moduleResponse.addBreadcrumbFirst(getBreadcrumb());
						}

						List<BackgroundModuleResponse> backgroundModuleResponses = this.getBackgroundModuleResponses(req, user, uriParser);

						if (backgroundModuleResponses != null) {

							moduleResponse.addBackgroundModuleResponses(backgroundModuleResponses);
						}
					}

					return moduleResponse;

				} else {
					throw new AccessDeniedException(sectionCacheEntry.getKey());
				}
			}

			Entry<ForegroundModuleDescriptor, ForegroundModule> moduleCacheEntry;

			if (uriParser.size() >= 1 && (moduleCacheEntry = this.foregroundModuleCache.getEntry(uriParser.get(0))) != null) {

				if (AccessUtils.checkAccess(user, moduleCacheEntry.getKey())) {

					HTTPProtocol requiredHTTPProtocol = moduleCacheEntry.getKey().getRequiredProtocol();
					HTTPProtocol currentHTTPProtocol = req.isSecure() ? HTTPProtocol.HTTPS : HTTPProtocol.HTTP;

					try {

						// Switch to module required protocol
						if (requiredHTTPProtocol != null && !currentHTTPProtocol.equals(requiredHTTPProtocol)) {

							res.sendRedirect(requiredHTTPProtocol.toString().toLowerCase() + "://" + req.getServerName() + req.getContextPath() + uriParser.getFormattedURI());

							return null;

							// Switch to section required protocol
						}else if (requiredHTTPProtocol == null && enforcedHTTPProtocol != null && !currentHTTPProtocol.equals(enforcedHTTPProtocol)) {

							res.sendRedirect(enforcedHTTPProtocol.toString().toLowerCase() + "://" + req.getServerName() + req.getContextPath() + uriParser.getFormattedURI());

							return null;
						}

					} catch (IOException e) {

						throw new ProtocolRedirectException(this.sectionDescriptor, moduleCacheEntry.getKey(), e);
					}

					try {
						ForegroundModuleResponse moduleResponse = moduleCacheEntry.getValue().processRequest(req, res, user, uriParser);

						if (!res.isCommitted()) {

							if (moduleResponse != null) {

								// Check if the user has changed
								if (moduleResponse.isUserChanged()) {
									user = (User) req.getSession(true).getAttribute("user");
								}

								moduleResponse.setMenu(this.menuCache.getUserMenu(user,null,uriParser));

								if (moduleResponse.getResponseType() == ResponseType.XML_FOR_SEPARATE_TRANSFORMATION && moduleResponse.getTransformer() == null) {
									moduleResponse.setTransformer(this.foregroundModuleXSLTCache.getModuleTranformer(moduleCacheEntry.getKey()));
								}

								moduleResponse.setModuleDescriptor(moduleCacheEntry.getKey());

								if (this.sectionDescriptor.hasBreadCrumb() && !moduleResponse.isExcludeSectionBreadcrumbs()) {
									moduleResponse.addBreadcrumbFirst(getBreadcrumb());
								}

								List<BackgroundModuleResponse> backgroundModuleResponses = this.getBackgroundModuleResponses(req, user, uriParser);

								if (backgroundModuleResponses != null) {

									moduleResponse.addBackgroundModuleResponses(backgroundModuleResponses);
								}

							} else {

								throw new ForegroundNullResponseException();
							}
						}

						return moduleResponse;

					} catch (RequestException e) {

						e.setSectionDescriptor(sectionDescriptor);
						e.setModuleDescriptor(moduleCacheEntry.getKey());

						throw e;

					} catch (Throwable t) {

						throw new UnhandledModuleException(this.sectionDescriptor, moduleCacheEntry.getKey(), t);

					}
				} else {

					throw new AccessDeniedException(this.sectionDescriptor, moduleCacheEntry.getKey());
				}
			}

			if(isDefaultURI){

				throw new SectionDefaultURINotFoundException(sectionDescriptor, uriParser, user != null);
			}

			throw new URINotFoundException(this.sectionDescriptor, uriParser);

		} catch (RequestException e) {

			e.setMenu(this.menuCache.getUserMenu(user,e.getMenu(),uriParser));

			List<BackgroundModuleResponse> backgroundModuleResponses = this.getBackgroundModuleResponses(req, user, uriParser);

			if (backgroundModuleResponses != null) {

				e.addBackgroundModuleResponses(backgroundModuleResponses);
			}

			throw e;

		} finally {
			r.unlock();
		}
	}

	public Breadcrumb getBreadcrumb() {
		return new Breadcrumb(this.sectionDescriptor);
	}

	private List<BackgroundModuleResponse> getBackgroundModuleResponses(HttpServletRequest req, User user, URIParser uriParser) {

		List<BackgroundModuleResponse> bgResponses = null;

		List<Entry<BackgroundModuleDescriptor, BackgroundModule>> backgroundModuleEntries = this.backgroundModuleCache.getEntries(uriParser.getRemainingURI(), user);

		if (!CollectionUtils.isEmpty(backgroundModuleEntries)) {

			for (Entry<BackgroundModuleDescriptor, BackgroundModule> bgEntry : backgroundModuleEntries) {

				try {
					BackgroundModuleResponse response = bgEntry.getValue().processRequest(req, user, uriParser);

					if (response != null) {

						if (CollectionUtils.isEmpty(response.getSlots())) {

							response.setSlots(bgEntry.getKey().getSlots());
						}

						if (response.getResponseType() == ResponseType.XML_FOR_SEPARATE_TRANSFORMATION && response.getTransformer() == null) {

							Transformer transformer = this.backgroundModuleXSLTCache.getModuleTranformer(bgEntry.getKey());

							if (transformer != null) {

								response.setTransformer(transformer);
							}
						}

						response.setModuleDescriptor(bgEntry.getKey());

						if (bgResponses == null) {

							bgResponses = new ArrayList<BackgroundModuleResponse>();
						}

						bgResponses.add(response);
					}

				} catch (Throwable t) {

					log.error("Error thrown from background module " + bgEntry.getKey() + " in section " + this.sectionDescriptor + " while processing request for user " + user, t);
				}
			}
		}
		return bgResponses;
	}

	public int getReadLockCount() {

		return rwl.getReadLockCount();
	}

	public ForegroundModuleCache getForegroundModuleCache() {

		return foregroundModuleCache;
	}

	public BackgroundModuleCache getBackgroundModuleCache() {

		return backgroundModuleCache;
	}

	public MenuItemCache getMenuCache() {

		return menuCache;
	}

	public ForegroundModuleXSLTCache getModuleXSLTCache() {

		return foregroundModuleXSLTCache;
	}

	public SectionCache getSectionCache() {

		return sectionCache;
	}

	public FullSystemInterface getSystemInterface() {

		return systemInterface;
	}

	public SectionDescriptor getSectionDescriptor() {

		try {
			r.lock();
			return sectionDescriptor;
		} finally {
			r.unlock();
		}
	}

	public SectionInterface getParentSectionInterface() {

		return parentSectionInterface;
	}

	public SectionMenu getFullMenu(User user, URIParser uriParser) {

		r.lock();

		try{

			SectionMenu sectionMenu = menuCache.getUserMenu(user, null, uriParser);

			if(!sectionMenu.getMenuItems().isEmpty()){

				int index = 0;

				while(index < sectionMenu.getMenuItems().size()){

					MenuItem menuItem = sectionMenu.getMenuItems().get(index);

					//Check if this menuitem represents a section
					if(menuItem instanceof SectionMenuItem){

						Integer sectionID = ((SectionMenuItem)menuItem).getSubSectionID();

						Entry<SectionDescriptor, Section> entry = sectionCache.getEntry(sectionID);

						if(entry != null){

							Section subSection = sectionCache.getSectionInstance(entry.getKey());

							if(subSection != null){

								URIParser sentURIParser;

								if(uriParser != null && menuItem.isSelected()){

									sentURIParser = uriParser.getNextLevel();

								}else{

									sentURIParser = null;
								}

								//Replace previous menuitem for this index with new one containing the submenus for the relevant section
								SectionMenu subSectionMenu = subSection.getFullMenu(user, sentURIParser);

								menuItem = ((SectionMenuItem)menuItem).clone(subSectionMenu, false);

								sectionMenu.getMenuItems().set(index, menuItem);
							}
						}
					}

					index++;
				}
			}

			return sectionMenu;

		}finally{

			r.unlock();
		}
	}

}
