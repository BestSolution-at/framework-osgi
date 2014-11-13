package at.bestsolution.framework.osgi.equinox.debug;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;

import org.eclipse.osgi.internal.debug.Debug;
import org.eclipse.osgi.internal.framework.EquinoxConfiguration;
import org.eclipse.osgi.internal.hookregistry.ClassLoaderHook;
import org.eclipse.osgi.internal.loader.BundleLoader;
import org.eclipse.osgi.internal.loader.ModuleClassLoader;
import org.eclipse.osgi.internal.loader.classpath.ClasspathManager;
import org.eclipse.osgi.storage.BundleInfo.Generation;

public class DebugClassLoaderHook extends ClassLoaderHook {
	private Properties properties;
	private Pattern prefindClassPattern;
	private File outFile;

	public DebugClassLoaderHook() {
		System.err.println("Classloader hook is active");
		properties = new Properties();
		InputStream in = null;
		try {
			in = new FileInputStream(new File(System.getProperty("classloader.debug.config")));
			properties.load(in);

			if( properties.getProperty("logfile") != null ) {
				System.err.println("The log file is: " + properties.getProperty("logfile"));
				outFile = new File(properties.getProperty("logfile"));
			}

			if( properties.getProperty("classpattern") != null ) {
				println("Compiling search pattern: " + properties.getProperty("classpattern"));
				prefindClassPattern = Pattern.compile(properties.getProperty("classpattern"));
			}
		} catch( Throwable e ) {
			e.printStackTrace();
		}
	}

	public void println(String message) {
		if( outFile == null ) {
			System.out.println(message);
		} else {
			try {
				FileWriter w = new FileWriter(outFile,true);
				w.write(message + "\n");
				w.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private boolean trackFind(String name) {
		if( prefindClassPattern == null ) {
			return false;
		}

		return prefindClassPattern.matcher(name).find();
	}

	@Override
	public ModuleClassLoader createClassLoader(ClassLoader parent,
			EquinoxConfiguration configuration, BundleLoader delegate,
			Generation generation) {
		return new DelegatingModuleClassloader(parent, configuration, delegate, generation);
	}

	private static int nesting = 0;
	private static final String oneInset = "  ";

	private static String inset() {
		StringBuilder b = new StringBuilder();
		for( int i = 0; i < nesting; i++ ) {
			b.append(oneInset);
		}
		return b.toString();
	}

	class DelegatingModuleClassloader extends ModuleClassLoader {
		private final EquinoxConfiguration configuration;
		private final BundleLoader delegate;
		private final Generation generation;

		private ClasspathManager classpathManager;

		public DelegatingModuleClassloader(ClassLoader parent, EquinoxConfiguration configuration,
				BundleLoader delegate, final Generation generation) {
			super(parent);
			this.configuration = configuration;
			this.delegate = delegate;
			this.generation = generation;
			this.classpathManager = new ClasspathManager(generation, this) {
				@Override
				public Class<?> findLocalClass(String classname)
						throws ClassNotFoundException {

					if( ! trackFind(classname) ) {
						return super.findLocalClass(classname);
					}

					nesting++;
					println(inset() + "> BEGIN: findLocalClass '"+classname+"' from bundle " + generation.getRevision().getBundle().getSymbolicName());
					try {
						Class<?> c = super.findLocalClass(classname);
						if( c != null ) {
							println( inset() + oneInset +"Loaded class by '"+c.getClassLoader()+"'");
						}
						return c;
					} catch( RuntimeException e ) {
						throw e;
					} finally {
						println(inset() + "< END findLocalClass '"+classname+"'");
						nesting--;
					}

				}
			};
		}



		@Override
		protected Generation getGeneration() {
			return generation;
		}

		@Override
		protected Debug getDebug() {
			return this.configuration.getDebug();
		}

		@Override
		public ClasspathManager getClasspathManager() {
			return classpathManager;
		}

		@Override
		protected EquinoxConfiguration getConfiguration() {
			return this.configuration;
		}

		@Override
		public BundleLoader getBundleLoader() {
			return delegate;
		}

		@Override
		public boolean isRegisteredAsParallel() {
			return false;
		}

	}
}
