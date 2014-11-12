package at.bestsolution.framework.osgi.equinox.debug;

import org.eclipse.osgi.internal.hookregistry.ClassLoaderHook;
import org.eclipse.osgi.internal.loader.ModuleClassLoader;

public class DebugClassLoaderHook extends ClassLoaderHook {
	@Override
	public Class<?> preFindClass(String name, ModuleClassLoader classLoader)
			throws ClassNotFoundException {
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

	@Override
	public Class<?> postFindClass(String name, ModuleClassLoader classLoader)
			throws ClassNotFoundException {
		System.err.println("POST: Searching class '"+name+"' in " + classLoader);
		try {
			Class<?> postFindClass = super.postFindClass(name, classLoader);
			if( postFindClass != null ) {
				System.err.println("POST: Found class and loaded with '"+postFindClass.getClassLoader()+"'");
			}
			return postFindClass;
		} catch( RuntimeException e ) {
			System.err.println("POST: Failure while loading class '"+name+"'");
			e.printStackTrace();
			throw e;
		}

	}


//	@Override
//	public ModuleClassLoader createClassLoader(ClassLoader parent,
//			EquinoxConfiguration configuration, BundleLoader delegate,
//			Generation generation) {
//		ModuleClassLoader originalLoader = super.createClassLoader(parent, configuration, delegate, generation);
//
//		return new DebuggingClassloader(parent);
//	}

//	static class DebuggingClassloader extends ModuleClassLoader {
//		private ModuleClassLoader originalLoader;
//
//		public DebuggingClassloader(ClassLoader parent) {
//			super(parent);
//		}
//
//		@Override
//		protected Generation getGeneration() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		protected Debug getDebug() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public ClasspathManager getClasspathManager() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		protected EquinoxConfiguration getConfiguration() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public BundleLoader getBundleLoader() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public boolean isRegisteredAsParallel() {
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//	}
}
