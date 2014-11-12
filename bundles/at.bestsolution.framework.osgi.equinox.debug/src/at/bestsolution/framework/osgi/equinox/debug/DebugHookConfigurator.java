package at.bestsolution.framework.osgi.equinox.debug;

import org.eclipse.osgi.internal.hookregistry.HookConfigurator;
import org.eclipse.osgi.internal.hookregistry.HookRegistry;

public class DebugHookConfigurator implements HookConfigurator {
	public DebugHookConfigurator() {
		System.err.println("Debug hook is active");
	}

	@Override
	public void addHooks(HookRegistry hookRegistry) {
		hookRegistry.addClassLoaderHook(new DebugClassLoaderHook());
	}

}
