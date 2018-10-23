package com.itemis.gef.tutorial.mindmap.lifecycle;

import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import com.itemis.gef.tutorial.mindmap.SimpleMindMapModule;
import com.itemis.gef.tutorial.mindmap.SimpleMindMapUiModule;

@SuppressWarnings("restriction")
public class Manager {

	private static Injector injector;

	public static Injector getInjector() {
		return injector;
	}

	@PostContextCreate
	void postContextCreate(IApplicationContext appContext, Display display) {
		injector = Guice.createInjector(Modules.override(new SimpleMindMapModule()).with(new SimpleMindMapUiModule()));
	}
}
