package at.bestsolution.framework.osgi.equinox.debug;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;

import org.eclipse.osgi.internal.hookregistry.ClassLoaderHook;
import org.eclipse.osgi.internal.loader.ModuleClassLoader;

public class DebugClassLoaderHook extends ClassLoaderHook {
	private Properties properties;
	private Pattern prefindClassPattern;

	public DebugClassLoaderHook() {
		properties = new Properties();
		InputStream in = null;
		try {
			in = new FileInputStream(new File(System.getProperty("classloader.debug.config")));
			properties.load(in);

			if( properties.getProperty("pre.find.classpattern") != null ) {
				System.err.println("Compiling: " + properties.getProperty("pre.find.classpattern"));
				prefindClassPattern = Pattern.compile(properties.getProperty("pre.find.classpattern"));
			}
		} catch( Throwable e ) {
			e.printStackTrace();
		}
	}

	private boolean trackFind(String name) {
		if( prefindClassPattern == null ) {
			return false;
		}

		return prefindClassPattern.matcher(name).find();
	}

	@Override
	public Class<?> preFindClass(String name, ModuleClassLoader classLoader)
			throws ClassNotFoundException {
		if( ! trackFind(name) ) {
			return super.preFindClass(name, classLoader);
		}

		System.err.println("PRE: Searching class '"+name+"' in " + classLoader);
		try {
			Class<?> preFindClass = super.preFindClass(name, classLoader);
			if( preFindClass != null ) {
				System.err.println("PRE: Found class and loaded with '"+preFindClass.getClassLoader()+"'");
			}
			return preFindClass;
		} catch(RuntimeException e) {
			System.err.println("PRE: Failure while loading class '"+name+"'");
			e.printStackTrace();
			throw e;
		}
	}
}
