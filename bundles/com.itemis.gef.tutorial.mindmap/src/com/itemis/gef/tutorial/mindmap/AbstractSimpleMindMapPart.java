package com.itemis.gef.tutorial.mindmap;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.fx.swt.canvas.IFXCanvasFactory;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.ui.parts.AbstractFXView;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.itemis.gef.tutorial.mindmap.lifecycle.Manager;

import javafx.embed.swt.FXCanvas;

public class AbstractSimpleMindMapPart {
	@Inject
	protected IDomain domain;

	@Inject
	private IFXCanvasFactory canvasFactory;

	private FXCanvas fxCanvas = null;

	/**
	 * Constructs a new {@link AbstractFxComponent} that uses the given
	 * {@link Injector} to inject its members.
	 *
	 * @param injector
	 *            The {@link Injector} that is used to inject the members of this
	 *            {@link AbstractFxComponent}.
	 */
	public AbstractSimpleMindMapPart() {
		Manager.getInjector().injectMembers(this);
	}

	/**
	 * Activates this {@link AbstractFXView} by activating the {@link IDomain} that
	 * was previously injected.
	 */
	protected void activate() {
		domain.activate();
	}

	protected FXCanvas createCanvas(final Composite parent) {
		return canvasFactory.createCanvas(parent, SWT.NONE);
	}

	/**
	 * Deactivates this {@link AbstractFXView} by deactivating its {@link IDomain}
	 * that was previously injected.
	 */
	protected void deactivate() {
		domain.deactivate();
	}

	public void dispose() {
		// deactivate domain
		deactivate();

		// unhook selection forwarder
		unhookViewers();

		domain.dispose();
		domain = null;

		canvasFactory = null;
		if (!fxCanvas.isDisposed()) {
			fxCanvas.dispose();
		}
		fxCanvas = null;

	}

	/**
	 * Returns the {@link IViewer} of the {@link IDomain} that was previously
	 * injected.
	 *
	 * @return The {@link IViewer} of the {@link IDomain} that was previously
	 *         injected.
	 */
	public IViewer getContentViewer() {
		return domain.getAdapter(AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE));
	}

	/**
	 * Returns the {@link IDomain} that was previously injected.
	 *
	 * @return The {@link IDomain} that was previously injected.
	 */
	public IDomain getDomain() {
		return domain;
	}

	/**
	 * Returns the {@link FXCanvas} that was previously created by the injected
	 * {@link IFXCanvasFactory}.
	 *
	 * @return The {@link FXCanvas} that was previously created by the injected
	 *         {@link IFXCanvasFactory}.
	 */
	protected FXCanvas getFxCanvas() {
		return fxCanvas;
	}

	/**
	 * Hooks all viewers that are part of this {@link AbstractFXView} into the
	 * {@link FXCanvas} that was previously created by the injected
	 * {@link IFXCanvasFactory}.
	 */
	protected void hookViewers() {
		// by default we only have a single (content) viewer, so hook its
		// visuals as root visuals into the scene
		// fxCanvas.setScene(new Scene(getContentViewer().getCanvas()));
	}

	/**
	 * Create the canvas, hook up viewer and activate the domain.
	 *
	 * @param parent
	 *            Composite to contain the JavaFX Canvas
	 */
	protected void postConstruct(final Composite parent) {
		// create viewer and canvas only after toolkit has been initialized
		fxCanvas = createCanvas(parent);

		// // hook viewer controls and selection forwarder
		// hookViewers();
		//
		// // activate domain
		// activate();
	}

	protected void unhookViewers() {
		// TODO: What about taking the visuals out of the canvas?
	}

}
