package se.unlogic.hierarchy.core.utils;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import se.unlogic.hierarchy.core.enums.SystemStatus;
import se.unlogic.hierarchy.core.interfaces.ForegroundModule;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleCacheListener;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.hierarchy.core.interfaces.SystemStartupListener;

/**
 * @author Robert "Unlogic" Olofsson
 *
 * This class finds and keeps track of any started foreground modules implementing the given class or interface
 *
 * @param <T>
 */
public class MultiForegroundModuleTracker<T> implements ForegroundModuleCacheListener, SystemStartupListener {

	protected final Class<T> targetClass;
	protected final SectionInterface baseSection;
	protected final SystemInterface systemInterface;
	protected final boolean recursive;
	protected final boolean assignable;
	protected final ConcurrentHashMap<ForegroundModuleDescriptor, T> moduleMap = new ConcurrentHashMap<ForegroundModuleDescriptor, T>();

	public MultiForegroundModuleTracker(Class<T> targetClass, SystemInterface systemInterface, SectionInterface baseSection, boolean recursive, boolean assignable) {

		this.targetClass = targetClass;
		this.systemInterface = systemInterface;
		this.baseSection = baseSection;
		this.recursive = recursive;
		this.assignable = assignable;

		if(systemInterface.getSystemStatus() == SystemStatus.STARTING){

			systemInterface.addStartupListener(this);

		}else{

			systemStarted();
		}
	}

	public void systemStarted() {

		//Scan all loaded foreground modules
		ModuleUtils.findForegroundModules(targetClass, recursive, assignable, baseSection, moduleMap);

		//Add global foreground module listener
		systemInterface.addForegroundModuleCacheListener(this);
	}

	@SuppressWarnings("unchecked")
	public void moduleCached(ForegroundModuleDescriptor moduleDescriptor, ForegroundModule moduleInstance) {

		if(moduleInstance.getClass().equals(targetClass) || (assignable && targetClass.isAssignableFrom(moduleInstance.getClass()))){

			moduleMap.put(moduleDescriptor, (T)moduleInstance);
		}

	}

	@SuppressWarnings("unchecked")
	public void moduleUpdated(ForegroundModuleDescriptor moduleDescriptor, ForegroundModule moduleInstance) {

		if(moduleMap.contains(moduleDescriptor)){

			//Update the map with the latest module descriptor
			moduleMap.remove(moduleDescriptor, moduleInstance);
			moduleMap.put(moduleDescriptor, (T)moduleInstance);
		}
	}

	public void moduleUnloaded(ForegroundModuleDescriptor moduleDescriptor, ForegroundModule moduleInstance) {

		this.moduleMap.remove(moduleDescriptor);
	}

	public void shutdown() {

		systemInterface.removeForegroundModuleCacheListener(this);
		this.moduleMap.clear();
	}

	public Set<ForegroundModuleDescriptor> getDescriptors(){

		return moduleMap.keySet();
	}

	public Collection<T> getInstances(){

		return moduleMap.values();
	}

	public Set<Entry<ForegroundModuleDescriptor, T>> getEntries(){

		return moduleMap.entrySet();
	}

	public boolean isEmpty() {

		return moduleMap.isEmpty();
	}

	public int size() {

		return moduleMap.size();
	}
}
