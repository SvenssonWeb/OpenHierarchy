package se.unlogic.hierarchy.core.daos.interfaces;

import java.sql.SQLException;
import java.util.List;

import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;


public interface SectionModuleDAO<T extends ModuleDescriptor> extends ModuleDAO<T> {

	public abstract List<T> getEnabledModules(SectionDescriptor sectionDescriptor) throws SQLException;

	public abstract List<T> getModules(SectionDescriptor sectionDescriptor) throws SQLException;
}
