package es.gob.afirma.standalone.ui.restoreconfig;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.standalone.SimpleAfirmaMessages;

/**
 * Clase que gestiona los componentes gr&aacute;ficos de las ventanas de restauraci&oacute;n de la
 * configuraci&oacute;n de navegadores de AutoFirma.
 *
 */
public final class RestoreConfigPanel extends JPanel implements KeyListener, DisposableInterface {

	/**
	 * Identificador de la versi&oacute;n de serializaci&oacute;n.
	 */
	private static final long serialVersionUID = 5353477830742383848L;

	static final Logger LOGGER = Logger.getLogger("es.gob.afirma"); //$NON-NLS-1$

	private final Window window;

	/**
	 * &Aacute;rea de texto para mostrar los mensajes de progreso de la tarea de restauraci&oacute;n
	 */
	JTextArea taskOutput;


	/**Constructor con par&aacute;metro de la clase
	 * @param w Objeto Window para inicializar la instancia de la clase
	 */
	RestoreConfigPanel(final Window w) {

		this.window = w;
		createUI();
	}

	/**
	 * Dibuja la ventana con las opciones de restauraci&oacute;n
	 */
	private void createUI() {

		setLayout(new GridBagLayout());

		final GridBagConstraints c = new GridBagConstraints();


        // Creamos un panel para el texto de introduccion
		final JLabel introText = new JLabel(SimpleAfirmaMessages.getString("RestoreConfigPanel.4")); //$NON-NLS-1$

    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.insets = new Insets(15,  15,  11,  15);
    	c.weightx = 1.0;
    	c.gridy++;
        this.add(introText, c);

    	// Configuramos el area de texto para los mensajes de configuracion
    	this.taskOutput = new JTextArea();
    	this.taskOutput.setMargin(new Insets(5,5,5,5));
    	this.taskOutput.setEditable(false);
    	this.taskOutput.setLineWrap(true);
    	final JScrollPane jsPanel = new JScrollPane(
    			this.taskOutput,
    			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
    			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    	c.fill = GridBagConstraints.BOTH;
    	c.insets = new Insets(0, 15,  0,  15);
    	c.weighty = 1.0;
    	c.gridy++;
        this.add(jsPanel, c);

        // Creamos un panel para el boton de restauracion
        final JPanel buttonsPanel = createButtonsPanel();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(11, 15,  15,  15);
		c.gridy++;
		c.weighty = 0.0;
		c.ipady = 11;
		add(buttonsPanel, c);
	}

	/**
	 * Clase que construye el objeto gr&aacute;fico que representa el panel
	 * donde se ubican los botones de la ventana de restauraci&oacute;n
	 * @return
	 */
	private JPanel createButtonsPanel() {

		final JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.TRAILING));

		final JButton closeButton = new JButton(SimpleAfirmaMessages.getString("RestoreConfigPanel.5")); //$NON-NLS-1$
		closeButton.setMnemonic('C');
		closeButton.getAccessibleContext().setAccessibleDescription(
			SimpleAfirmaMessages.getString("RestoreConfigPanel.6") //$NON-NLS-1$
		);
		closeButton.addKeyListener(this);
		closeButton.addActionListener(
			new ActionListener() {
			    /** {@inheritDoc} */
	            @Override
	            public void actionPerformed(final ActionEvent ae) {
	            	disposeInterface();
	            }
	        }
		);

		final JButton restoreButton = new JButton(SimpleAfirmaMessages.getString("RestoreConfigPanel.1")); //$NON-NLS-1$
		restoreButton.setMnemonic('R');
		restoreButton.getAccessibleContext().setAccessibleDescription(
				SimpleAfirmaMessages.getString("RestoreConfigPanel.2") //$NON-NLS-1$
		);
		restoreButton.addKeyListener(this);
		restoreButton.addActionListener(new ActionListener() {
			/** {@inheritDoc} */
			@Override
			public void actionPerformed(final ActionEvent ae) {

			 	final RestoreConfigManager restoreConfig = new RestoreConfigManager();

				try {
					// Limpiamos el area de texto antes de comenzar con la restauracion
					// para eliminar posibles mensajes de ejecuciones anteriores.
					RestoreConfigPanel.this.taskOutput.setText(null);
					// Deshabilito el boton mientras el proceso se esta ejecutando
					restoreButton.setEnabled(false);
					restoreConfig.restoreConfigAutoFirma(RestoreConfigPanel.this.taskOutput);
					restoreButton.setEnabled(true);

				} catch (GeneralSecurityException | ConfigurationException | IOException e ) {
					LOGGER.severe("Ha ocurrido un error al ejecutar la tarea de restauracion: " + e.getMessage()); //$NON-NLS-1$
				}
			}
		});


		// En Mac OS X el orden de los botones es distinto
		if (Platform.OS.MACOSX.equals(Platform.getOS())) {
			panel.add(closeButton);
			panel.add(restoreButton);
		}
		else {
			panel.add(restoreButton);
			panel.add(closeButton);
		}
		return panel;
	}

	/**
	 * Devuelve la ventana desde donde se abri&oacute; la ventana actual
	 * @return
	 */
	Window getParentWindow() {
		return this.window;
	}

	@Override
	public void disposeInterface() {
		RestoreConfigPanel.this.getParentWindow().dispose();
	}

	/** {@inheritDoc} */
	@Override
	public void keyPressed(final KeyEvent e) {
		/* Vacio */ }

	/** {@inheritDoc} */
	@Override
	public void keyReleased(final KeyEvent ke) {
		// En Mac no cerramos los dialogos con Escape
		if (ke != null && ke.getKeyCode() == KeyEvent.VK_ESCAPE && !Platform.OS.MACOSX.equals(Platform.getOS())) {
			disposeInterface();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void keyTyped(final KeyEvent e) {
		/* Vacio */ }



}
