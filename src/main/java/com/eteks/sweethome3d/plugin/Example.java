//package com.eteks.sweethome3d.plugin.custom;
//
//import java.awt.BorderLayout;
//import java.awt.Component;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.Insets;
//import java.awt.event.ActionEvent;
//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
//import java.io.File;
//import java.io.IOException;
//import java.io.InterruptedIOException;
//import java.lang.ref.WeakReference;
//import java.net.URL;
//import java.util.Arrays;
//import java.util.Locale;
//import java.util.ResourceBundle;
//import java.util.concurrent.Callable;
//
//import javax.jnlp.BasicService;
//import javax.jnlp.ServiceManager;
//import javax.jnlp.UnavailableServiceException;
//import javax.media.j3d.Appearance;
//import javax.media.j3d.BranchGroup;
//import javax.media.j3d.Material;
//import javax.media.j3d.Shape3D;
//import javax.media.j3d.TransparencyAttributes;
//import javax.swing.AbstractAction;
//import javax.swing.ImageIcon;
//import javax.swing.JButton;
//import javax.swing.JComponent;
//import javax.swing.JEditorPane;
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JSlider;
//import javax.swing.event.HyperlinkEvent;
//import javax.swing.event.HyperlinkListener;
//import javax.swing.undo.AbstractUndoableEdit;
//import javax.swing.undo.UndoableEditSupport;
//import javax.vecmath.Color3f;
//import javax.vecmath.Point3f;
//import javax.vecmath.TexCoord2f;
//import javax.vecmath.Vector3f;
//
//import com.eteks.sweethome3d.j3d.ModelManager;
//import com.eteks.sweethome3d.j3d.OBJWriter;
//import com.eteks.sweethome3d.model.CatalogPieceOfFurniture;
//import com.eteks.sweethome3d.model.Content;
//import com.eteks.sweethome3d.model.HomePieceOfFurniture;
//import com.eteks.sweethome3d.model.InterruptedRecorderException;
//import com.eteks.sweethome3d.model.LengthUnit;
//import com.eteks.sweethome3d.model.RecorderException;
//import com.eteks.sweethome3d.model.UserPreferences;
//import com.eteks.sweethome3d.plugin.Plugin;
//import com.eteks.sweethome3d.plugin.PluginAction;
//import com.eteks.sweethome3d.swing.NullableSpinner;
//import com.eteks.sweethome3d.swing.SwingViewFactory;
//import com.eteks.sweethome3d.tools.OperatingSystem;
//import com.eteks.sweethome3d.tools.ResourceURLContent;
//import com.eteks.sweethome3d.tools.TemporaryURLContent;
//import com.eteks.sweethome3d.viewcontroller.HomeView;
//import com.eteks.sweethome3d.viewcontroller.ThreadedTaskController;
//import com.sun.j3d.utils.geometry.GeometryInfo;
//import com.sun.j3d.utils.geometry.NormalGenerator;
//
///**
// * A plug-in that creates a 3D shape from coordinates specified by user.
// *
// * @author Emmanuel Puybaret
// */
//public class Example extends Plugin {
//    private JPanel shapePanel;
//    private NullableSpinner.NullableSpinnerLengthModel[][] coordinatesSpinnerModels;
//    private JSlider transparencySlider;
//
//    @Override
//    public PluginAction[] getActions() {
//        return new PluginAction[]{new PluginAction(
//                "com.eteks.sweethome3d.plugin.shapegenerator.ApplicationPlugin",
//                "GENERATE_SHAPE", getPluginClassLoader(), true) {
//            @Override
//            public void execute() {
//                generateShape();
//            }
//        }
//        };
//    }
//
//    /**
//     * Creates a 3D shape from coordinates specified by user.
//     */
//    private void generateShape() {
//        final ResourceBundle resource = ResourceBundle.getBundle(
//                "com.eteks.sweethome3d.plugin.shapegenerator.ApplicationPlugin",
//                Locale.getDefault(), getPluginClassLoader());
//        final HomeView homeView = getHomeController().getView();
//
//        try {
//            // Ignore plug-in in protected Java Web Start environment
//            ServiceManager.lookup("javax.jnlp.FileSaveService");
//            // Use an uneditable editor pane to let user select text in dialog
//            JEditorPane messagePane = new JEditorPane("text/html",
//                    resource.getString("shapeGeneratorJavaWebStartInfo.message"));
//            messagePane.setOpaque(false);
//            messagePane.setEditable(false);
//            try {
//                // Lookup the javax.jnlp.BasicService object
//                final BasicService service = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
//                // If basic service supports  web browser
//                if (service.isWebBrowserSupported()) {
//                    // Add a listener that displays hyperlinks content in browser
//                    messagePane.addHyperlinkListener(new HyperlinkListener() {
//                        public void hyperlinkUpdate(HyperlinkEvent ev) {
//                            if (ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
//                                service.showDocument(ev.getURL());
//                            }
//                        }
//                    });
//                }
//            } catch (UnavailableServiceException ex) {
//                // Too bad : service is unavailable
//            }
//
//            String title = resource.getString("shapeGeneratorJavaWebStartInfo.title");
//            JOptionPane.showMessageDialog((JComponent) homeView, messagePane, title, JOptionPane.WARNING_MESSAGE);
//            return;
//        } catch (UnavailableServiceException ex) {
//        }
//
//        if (this.shapePanel == null) {
//            // Reuse the same panel from a call to plug-in to the next to allow modifying values if they were wrong
//            createComponents(resource);
//            // Reset panel if unit changes
//            getUserPreferences().addPropertyChangeListener(UserPreferences.Property.UNIT, new UserPreferencesChangeListener(this));
//        }
//        if (JOptionPane.showConfirmDialog((JComponent) homeView, this.shapePanel,
//                resource.getString("GENERATE_SHAPE.NAME"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
//            final float[][] points = new float[this.coordinatesSpinnerModels.length][3];
//            for (int i = 0; i < points.length; i++) {
//                points[i][0] = this.coordinatesSpinnerModels[i][0].getLength();
//                points[i][2] = this.coordinatesSpinnerModels[i][1].getLength();
//                points[i][1] = this.coordinatesSpinnerModels[i][2].getLength();
//            }
//            final float alpha = this.transparencySlider.getValue() / 255f;
//
//            // Create a new 3D model in a threaded task
//            Callable<Void> exportToObjTask = new Callable<Void>() {
//                public Void call() throws RecorderException {
//                    final String shapeName = resource.getString("untitledShapeName");
//                    final Content modelContent = exportShape(shapeName, points, alpha);
//                    float minX = Float.POSITIVE_INFINITY;
//                    float maxX = Float.NEGATIVE_INFINITY;
//                    float minY = Float.POSITIVE_INFINITY;
//                    float maxY = Float.NEGATIVE_INFINITY;
//                    float minElevation = Float.POSITIVE_INFINITY;
//                    for (float[] point : points) {
//                        minX = Math.min(minX, point[0]);
//                        maxX = Math.max(maxX, point[0]);
//                        minY = Math.min(minY, point[2]);
//                        maxY = Math.max(maxY, point[2]);
//                        minElevation = Math.min(minElevation, point[1]);
//                    }
//                    final float elevation = minElevation;
//                    final float x = (minX + maxX) / 2;
//                    final float y = (minY + maxY) / 2;
//                    homeView.invokeLater(new Runnable() {
//                        public void run() {
//                            if (modelContent != null) {
//                                addNewPieceOfFurniture(modelContent, shapeName,
//                                        x, y, elevation, resource);
//                            } else {
//                                JOptionPane.showMessageDialog((JComponent) homeView,
//                                        resource.getString("emptyShapeError.message"),
//                                        resource.getString("shapeGeneratorError.title"), JOptionPane.ERROR_MESSAGE);
//                            }
//                        }
//                    });
//                    return null;
//                }
//            };
//            ThreadedTaskController.ExceptionHandler exceptionHandler =
//                    new ThreadedTaskController.ExceptionHandler() {
//                        public void handleException(Exception ex) {
//                            if (!(ex instanceof InterruptedRecorderException)) {
//                                showError((JComponent) homeView, resource, ex.getMessage());
//                                ex.printStackTrace();
//                            }
//                        }
//                    };
//            new ThreadedTaskController(exportToObjTask,
//                    resource.getString("shapeGeneratorMessage"), exceptionHandler,
//                    getUserPreferences(), new SwingViewFactory()).executeTask(homeView);
//        }
//    }
//
//    /**
//     * Creates and layout the components shown in the dialog box of this plug-in.
//     */
//    private void createComponents(ResourceBundle resource) {
//        UserPreferences preferences = getUserPreferences();
//        this.coordinatesSpinnerModels = new NullableSpinner.NullableSpinnerLengthModel[8][3];
//        for (int i = 0; i < this.coordinatesSpinnerModels.length; i++) {
//            this.coordinatesSpinnerModels[i][0] = new NullableSpinner.NullableSpinnerLengthModel(preferences, -10000f, 10000f);
//            this.coordinatesSpinnerModels[i][1] = new NullableSpinner.NullableSpinnerLengthModel(preferences, -10000f, 10000f);
//            this.coordinatesSpinnerModels[i][2] = new NullableSpinner.NullableSpinnerLengthModel(preferences, 0f, 10000f);
//        }
//        resetToCubeCoordinates(this.coordinatesSpinnerModels);
//        this.transparencySlider = new JSlider(0, 255);
//        this.transparencySlider.setPaintTicks(true);
//        this.transparencySlider.setMajorTickSpacing(17);
//        this.transparencySlider.setValue(0);
//        JButton resetButton = new JButton(new AbstractAction(resource.getString("resetButton.text")) {
//            public void actionPerformed(ActionEvent ev) {
//                resetToCubeCoordinates(coordinatesSpinnerModels);
//            }
//        });
//
//        // Layout coordinates spinners and transparency slider in a panel with labels
//        this.shapePanel = new JPanel(new GridBagLayout());
//        this.shapePanel.add(new JLabel(String.format(resource.getString("pointsPanel.comment"), preferences.getLengthUnit().getName())),
//                new GridBagConstraints(0, 0, 8, 1, 0, 0, GridBagConstraints.LINE_START,
//                        GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
//        this.shapePanel.add(new JLabel(new ImageIcon(ShapeGeneratorPlugin.class.getResource("shapeLabel.png"))),
//                new GridBagConstraints(0, 1, 1, 11, 0, 0, GridBagConstraints.NORTH,
//                        GridBagConstraints.NONE, new Insets(0, 0, 5, 10), 0, 0));
//        for (int i = 0; i < this.coordinatesSpinnerModels.length; i++) {
//            this.shapePanel.add(new JLabel(String.format(resource.getString("pointsPanel.pointLabel.text"), i + 1)),
//                    new GridBagConstraints(1, i + 1, 1, 1, 0, 0, GridBagConstraints.LINE_START,
//                            GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
//            this.shapePanel.add(new JLabel(resource.getString("pointsPanel.xLabel.text")),
//                    new GridBagConstraints(2, i + 1, 1, 1, 0, 0, GridBagConstraints.LINE_START,
//                            GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
//            this.shapePanel.add(new NullableSpinner(this.coordinatesSpinnerModels[i][0]),
//                    new GridBagConstraints(3, i + 1, 1, 1, 0, 0, GridBagConstraints.LINE_START,
//                            GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
//            this.shapePanel.add(new JLabel(resource.getString("pointsPanel.yLabel.text")),
//                    new GridBagConstraints(4, i + 1, 1, 1, 0, 0, GridBagConstraints.LINE_START,
//                            GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
//            this.shapePanel.add(new NullableSpinner(this.coordinatesSpinnerModels[i][1]),
//                    new GridBagConstraints(5, i + 1, 1, 1, 0, 0, GridBagConstraints.LINE_START,
//                            GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
//            this.shapePanel.add(new JLabel(resource.getString("pointsPanel.zLabel.text")),
//                    new GridBagConstraints(6, i + 1, 1, 1, 0, 0, GridBagConstraints.LINE_START,
//                            GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
//            this.shapePanel.add(new NullableSpinner(this.coordinatesSpinnerModels[i][2]),
//                    new GridBagConstraints(7, i + 1, 1, 1, 0, 0, GridBagConstraints.LINE_START,
//                            GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
//        }
//        this.shapePanel.add(new JLabel(resource.getString("transparencyLabel.text")), new GridBagConstraints(
//                1, 10, 1, 1, 0, 0, GridBagConstraints.LINE_START,
//                GridBagConstraints.NONE, new Insets(5, 0, 0, 5), 0, 0));
//        this.shapePanel.add(this.transparencySlider, new GridBagConstraints(
//                2, 10, 4, 1, 0, 0, GridBagConstraints.LINE_START,
//                GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
//        this.shapePanel.add(resetButton, new GridBagConstraints(
//                6, 10, 2, 1, 0, 0, GridBagConstraints.EAST,
//                GridBagConstraints.NONE, new Insets(5, 10, 0, 0), 0, 0));
//        JPanel transparencyLabelsPanel = new JPanel(new BorderLayout(20, 0));
//        transparencyLabelsPanel.setOpaque(false);
//        transparencyLabelsPanel.add(new JLabel(resource.getString("opaqueLabel.text")), BorderLayout.WEST);
//        transparencyLabelsPanel.add(new JLabel(resource.getString("invisibleLabel.text")), BorderLayout.EAST);
//        this.shapePanel.add(transparencyLabelsPanel, new GridBagConstraints(
//                2, 11, 4, 1, 0, 0, GridBagConstraints.NORTH,
//                GridBagConstraints.HORIZONTAL, new Insets(OperatingSystem.isWindows() ? 0 : -3, 0, 0, 0), 0, 0));
//    }
//
//    private void resetToCubeCoordinates(NullableSpinner.NullableSpinnerLengthModel[][] coordinatesSpinnerModels) {
//        // See shapeLabel.png for points order
//        LengthUnit lengthUnit = getUserPreferences().getLengthUnit();
//        float size = lengthUnit == LengthUnit.METER || lengthUnit == LengthUnit.CENTIMETER || lengthUnit == LengthUnit.MILLIMETER
//                ? 100f
//                : LengthUnit.INCH.unitToCentimeter(36);
//        final float[][] points = {
//                {0, 0, 0},
//                {size, 0, 0},
//                {size, 0, size},
//                {0, 0, size},
//                {0, size, 0},
//                {size, size, 0},
//                {size, size, size},
//                {0, size, size},
//        };
//        for (int i = 0; i < points.length; i++) {
//            coordinatesSpinnerModels[i][0].setLength(points[i][0]);
//            coordinatesSpinnerModels[i][1].setLength(points[i][2]);
//            coordinatesSpinnerModels[i][2].setLength(points[i][1]);
//        }
//    }
//
//    /**
//     * Exports the shape made of the 8 points and returns a content matching it.
//     */
//    private Content exportShape(String shapeName, float[][] points, float alpha) throws RecorderException {
//        try {
//            BranchGroup root = new BranchGroup();
//            Point3f[] vertices = new Point3f[8];
//            for (int i = 0; i < vertices.length; i++) {
//                vertices[i] = new Point3f(points[i][0], points[i][1], points[i][2]);
//            }
//            // See shapeLabel.png for points order
//            // Textures coordinates placed to view texture on front, left and top faces and mirrored texture on other faces
//            createShape(root,
//                    new Point3f[]{vertices[0], vertices[1], vertices[2], // Bottom
//                            vertices[0], vertices[2], vertices[3]},
//                    new TexCoord2f[]{new TexCoord2f(0, 0), new TexCoord2f((vertices[1].x - vertices[0].x) / 100, (vertices[1].z - vertices[0].z) / 100), new TexCoord2f((vertices[2].x - vertices[0].x) / 100, (vertices[2].z - vertices[0].z) / 100),
//                            new TexCoord2f(0, 0), new TexCoord2f((vertices[2].x - vertices[0].x) / 100, (vertices[2].z - vertices[0].z) / 100), new TexCoord2f((vertices[3].x - vertices[0].x) / 100, (vertices[3].z - vertices[0].z) / 100)},
//                    "bottom", alpha);
//            createShape(root,
//                    new Point3f[]{vertices[1], vertices[0], vertices[4], // Back
//                            vertices[1], vertices[4], vertices[5]},
//                    new TexCoord2f[]{new TexCoord2f(0, 0), new TexCoord2f((vertices[1].x - vertices[0].x) / 100, (vertices[0].y - vertices[1].y) / 100), new TexCoord2f((vertices[1].x - vertices[4].x) / 100, (vertices[4].y - vertices[1].y) / 100),
//                            new TexCoord2f(0, 0), new TexCoord2f((vertices[1].x - vertices[4].x) / 100, (vertices[4].y - vertices[1].y) / 100), new TexCoord2f((vertices[1].x - vertices[5].x) / 100, (vertices[5].y - vertices[1].y) / 100)},
//                    "back", alpha);
//            createShape(root,
//                    new Point3f[]{vertices[0], vertices[3], vertices[7], // Left
//                            vertices[0], vertices[7], vertices[4]},
//                    new TexCoord2f[]{new TexCoord2f(0, 0), new TexCoord2f((vertices[3].z - vertices[0].z) / 100, (vertices[3].y - vertices[0].y) / 100), new TexCoord2f((vertices[7].z - vertices[0].z) / 100, (vertices[7].y - vertices[0].y) / 100),
//                            new TexCoord2f(0, 0), new TexCoord2f((vertices[7].z - vertices[0].z) / 100, (vertices[7].y - vertices[0].y) / 100), new TexCoord2f((vertices[4].z - vertices[0].z) / 100, (vertices[4].y - vertices[0].y) / 100)},
//                    "left", alpha);
//            createShape(root,
//                    new Point3f[]{vertices[2], vertices[1], vertices[5], // Right
//                            vertices[2], vertices[5], vertices[6]},
//                    new TexCoord2f[]{new TexCoord2f(0, 0), new TexCoord2f((vertices[2].z - vertices[1].z) / 100, (vertices[1].y - vertices[2].y) / 100), new TexCoord2f((vertices[2].z - vertices[5].z) / 100, (vertices[5].y - vertices[2].y) / 100),
//                            new TexCoord2f(0, 0), new TexCoord2f((vertices[2].z - vertices[5].z) / 100, (vertices[5].y - vertices[2].y) / 100), new TexCoord2f((vertices[2].z - vertices[6].z) / 100, (vertices[6].y - vertices[2].y) / 100)},
//                    "right", alpha);
//            createShape(root,
//                    new Point3f[]{vertices[3], vertices[2], vertices[6], // Front
//                            vertices[3], vertices[6], vertices[7]},
//                    new TexCoord2f[]{new TexCoord2f(0, 0), new TexCoord2f((vertices[2].x - vertices[3].x) / 100, (vertices[2].y - vertices[3].y) / 100), new TexCoord2f((vertices[6].x - vertices[3].x) / 100, (vertices[6].y - vertices[3].y) / 100),
//                            new TexCoord2f(0, 0), new TexCoord2f((vertices[6].x - vertices[3].x) / 100, (vertices[6].y - vertices[3].y) / 100), new TexCoord2f((vertices[7].x - vertices[3].x) / 100, (vertices[7].y - vertices[3].y) / 100)},
//                    "front", alpha);
//            createShape(root,
//                    new Point3f[]{vertices[7], vertices[6], vertices[5], // Top
//                            vertices[7], vertices[5], vertices[4]},
//                    new TexCoord2f[]{new TexCoord2f(0, 0), new TexCoord2f((vertices[6].x - vertices[7].x) / 100, (vertices[7].z - vertices[6].z) / 100), new TexCoord2f((vertices[5].x - vertices[7].x) / 100, (vertices[7].z - vertices[5].z) / 100),
//                            new TexCoord2f(0, 0), new TexCoord2f((vertices[5].x - vertices[7].x) / 100, (vertices[7].z - vertices[5].z) / 100), new TexCoord2f((vertices[4].x - vertices[7].x) / 100, (vertices[7].z - vertices[4].z) / 100)},
//                    "top", alpha);
//
//            if (root.numChildren() != 0) {
//                File tempZipFile = OperatingSystem.createTemporaryFile(shapeName, ".zip");
//                String objFile = shapeName + ".obj";
//                OBJWriter.writeNodeInZIPFile(root, tempZipFile, 0, objFile, "Created by ShapeGenerator plug-in");
//                return new TemporaryURLContent(new URL("jar:" + tempZipFile.toURI().toURL() + "!/" + objFile));
//            } else {
//                return null;
//            }
//        } catch (InterruptedIOException ex) {
//            throw new InterruptedRecorderException("Export to OBJ interrupted");
//        } catch (IOException ex) {
//            throw new RecorderException("Couldn't export to OBJ", ex);
//        }
//    }
//
//    /**
//     * Adds to parent a 3D shape matching the coordinates if the shape isn't empty.
//     */
//    private void createShape(BranchGroup parent, Point3f[] coords, TexCoord2f[] textureCoords,
//                             String material, float alpha) {
//        if (!isTriangleEmpty(coords[0], coords[1], coords[2])
//                || !isTriangleEmpty(coords[3], coords[4], coords[5])) {
//            GeometryInfo geometryInfo = new GeometryInfo(GeometryInfo.TRIANGLE_ARRAY);
//            geometryInfo.setCoordinates(coords);
//            geometryInfo.setTextureCoordinateParams(1, 2);
//            geometryInfo.setTextureCoordinates(0, textureCoords);
//            new NormalGenerator(0).generateNormals(geometryInfo);
//
//            Appearance appearance = new Appearance();
//            try {
//                appearance.setName(material);
//            } catch (NoSuchMethodError ex) {
//                // Ignore appearance name with Java 3D < 1.4 where setName was added
//            }
//            appearance.setMaterial(new Material(new Color3f(0.2f, 0.2f, 0.2f), new Color3f(), new Color3f(1.0f, 1.0f, 1.0f), new Color3f(), 0));
//            if (alpha > 0) {
//                appearance.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, alpha));
//            }
//            parent.addChild(new Shape3D(geometryInfo.getIndexedGeometryArray(), appearance));
//        }
//    }
//
//    /**
//     * Returns <code>true</code> if the surface of the triangle (p1, p2, p3) is zero.
//     */
//    private boolean isTriangleEmpty(Point3f p1, Point3f p2, Point3f p3) {
//        Vector3f vector1 = new Vector3f(p2);
//        vector1.sub(p1);
//        Vector3f vector2 = new Vector3f(p3);
//        vector2.sub(p1);
//        Vector3f vector = new Vector3f();
//        vector.cross(vector1, vector2);
//        return vector.lengthSquared() < 1E-10;
//    }
//
//    /**
//     * Adds to home a new piece of furniture based on the given model.
//     */
//    private void addNewPieceOfFurniture(final Content modelContent,
//                                        final String shapeName,
//                                        final float x,
//                                        final float y,
//                                        final float elevation,
//                                        final ResourceBundle resource) {
//        ModelManager.getInstance().loadModel(modelContent, new ModelManager.ModelObserver() {
//            public void modelUpdated(BranchGroup modelRoot) {
//                Vector3f size = ModelManager.getInstance().getSize(modelRoot);
//                Content iconContent = new ResourceURLContent(ShapeGeneratorPlugin.class, "shape.png");
//                HomePieceOfFurniture piece = new HomePieceOfFurniture(new CatalogPieceOfFurniture(
//                        null, shapeName, null, iconContent, modelContent,
//                        size.x, size.z, size.y,
//                        0, true, null, System.getProperty("user.name"), true, null, null));
//                piece.setX(x);
//                piece.setY(y);
//                piece.setElevation(elevation);
//
//                UndoableEditSupport undoSupport = getUndoableEditSupport();
//                undoSupport.beginUpdate();
//                getHomeController().getFurnitureController().addFurniture(Arrays.asList(new HomePieceOfFurniture[]{piece}));
//                undoSupport.postEdit(new AbstractUndoableEdit() {
//                    @Override
//                    public String getPresentationName() {
//                        return resource.getString("undoGenerateShape");
//                    }
//                });
//
//                // End compound edit
//                undoSupport.endUpdate();
//            }
//
//            public void modelError(Exception ex) {
//                // Shouldn't happen since we import a model we just exported
//            }
//        });
//    }
//
//    /**
//     * Shows a message error.
//     */
//    private void showError(Component parent,
//                           ResourceBundle resource,
//                           String messageDetail) {
//        String messageFormat = resource.getString("shapeGeneratorError.message");
//        JOptionPane.showMessageDialog(parent, String.format(messageFormat, messageDetail),
//                resource.getString("shapeGeneratorError.title"), JOptionPane.ERROR_MESSAGE);
//    }
//
//    /**
//     * Preferences property listener bound to this table with a weak reference to avoid
//     * strong link between user preferences and this plug-in.
//     */
//    private static class UserPreferencesChangeListener implements PropertyChangeListener {
//        private WeakReference<ShapeGeneratorPlugin> plugin;
//
//        public UserPreferencesChangeListener(ShapeGeneratorPlugin plugin) {
//            this.plugin = new WeakReference<ShapeGeneratorPlugin>(plugin);
//        }
//
//        public void propertyChange(PropertyChangeEvent ev) {
//            // If plugin was garbage collected, remove this listener from preferences
//            ShapeGeneratorPlugin plugin = this.plugin.get();
//            ((UserPreferences) ev.getSource()).removePropertyChangeListener(
//                    UserPreferences.Property.valueOf(ev.getPropertyName()), this);
//            if (plugin != null
//                    && plugin.shapePanel != null) {
//                plugin.shapePanel = null;
//            }
//        }
//    }
//}
