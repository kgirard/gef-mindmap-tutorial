
package com.itemis.gef.tutorial.mindmap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.mvc.fx.domain.HistoricizingDomain;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.swt.widgets.Composite;

import com.itemis.gef.tutorial.mindmap.model.SimpleMindMap;
import com.itemis.gef.tutorial.mindmap.model.SimpleMindMapExampleFactory;
import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel;
import com.itemis.gef.tutorial.mindmap.models.ItemCreationModel.Type;
import com.itemis.gef.tutorial.mindmap.visuals.MindMapNodeVisual;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SimpleMindMapPart extends AbstractSimpleMindMapPart {

	private HistoricizingDomain histDomain;

	SimpleMindMapPart() {
		histDomain = (HistoricizingDomain) domain;
	}

	/**
	 * Creates the undo/redo buttons
	 *
	 * @return
	 */
	private Node createButtonBar() {
		Button undoButton = new Button("Undo");
		undoButton.setDisable(true);
		undoButton.setOnAction((e) -> {
			try {
				histDomain.getOperationHistory().undo(histDomain.getUndoContext(), null, null);
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
		});

		Button redoButton = new Button("Redo");
		redoButton.setDisable(true);
		redoButton.setOnAction((e) -> {
			try {
				histDomain.getOperationHistory().redo(histDomain.getUndoContext(), null, null);
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
		});

		// add listener to the operation history of our domain
		// to enable/disable undo/redo buttons as needed
		histDomain.getOperationHistory().addOperationHistoryListener((e) -> {
			IUndoContext ctx = histDomain.getUndoContext();
			undoButton.setDisable(!e.getHistory().canUndo(ctx));
			redoButton.setDisable(!e.getHistory().canRedo(ctx));
		});

		return new HBox(10, undoButton, redoButton);
	}

	private SimpleMindMap createMindMap() {
		SimpleMindMapExampleFactory fac = new SimpleMindMapExampleFactory();
		SimpleMindMap mindMap = fac.createComplexExample();
		return mindMap;
	}

	/**
	 * Creates the tooling buttons to create new elements
	 *
	 * @return
	 */
	private Node createToolPalette() {
		ItemCreationModel creationModel = getContentViewer().getAdapter(ItemCreationModel.class);

		MindMapNodeVisual graphic = new MindMapNodeVisual();
		graphic.setTitle("New Node");

		// the toggleGroup makes sure, we only select one
		ToggleGroup toggleGroup = new ToggleGroup();

		ToggleButton createNode = new ToggleButton("", graphic);
		createNode.setToggleGroup(toggleGroup);
		createNode.selectedProperty().addListener((e, oldVal, newVal) -> {
			creationModel.setType(newVal ? Type.Node : Type.None);
		});

		ToggleButton createConn = new ToggleButton("New Connection");
		createConn.setToggleGroup(toggleGroup);
		createConn.setMaxWidth(Double.MAX_VALUE);
		createConn.setMinHeight(50);
		createConn.selectedProperty().addListener((e, oldVal, newVal) -> {
			creationModel.setType(newVal ? Type.Connection : Type.None);
		});

		// now listen to changes in the model, and deactivate buttons, if
		// necessary
		creationModel.getTypeProperty().addListener((e, oldVal, newVal) -> {
			if (Type.None == newVal) {
				// unselect the toggle button
				Toggle selectedToggle = toggleGroup.getSelectedToggle();
				if (selectedToggle != null) {
					selectedToggle.setSelected(false);
				}
			}
		});

		return new VBox(20, createNode, createConn);
	}

	@Override
	@PostConstruct
	public void postConstruct(Composite parent) {
		super.postConstruct(parent);

		final IViewer viewer = getContentViewer();
		final InfiniteCanvas canvas = (InfiniteCanvas) viewer.getCanvas();
		// StackPane contentPane = new StackPane(canvas);
		//
		// Label paletteLabel = new Label("Testing");
		// SplitPane splitPane = new SplitPane(paletteLabel, contentPane);
		// splitPane.setDividerPosition(0, 20);

		BorderPane pane = new BorderPane();
		pane.setTop(createButtonBar());
		pane.setLeft(createToolPalette());
		pane.setCenter(canvas);

		SimpleMindMap mindMap = createMindMap();
		setViewerContents(mindMap);
		getFxCanvas().setScene(new Scene(pane));

		activate();
	}

	@PreDestroy
	private void preDestroy() {
		super.dispose();
	}

	@Focus
	public void setFocus() {
		getFxCanvas().setFocus();
	}

	private void setViewerContents(SimpleMindMap simpleMindMap) {
		final IViewer viewer = getContentViewer();
		viewer.getContents().setAll(simpleMindMap);
	}
}