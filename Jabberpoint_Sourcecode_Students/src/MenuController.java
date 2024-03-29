import java.awt.MenuBar;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.*;

/** <p>The controller for the menu</p>
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.1 2002/12/17 Gert Florijn
 * @version 1.2 2003/11/19 Sylvia Stuurman
 * @version 1.3 2004/08/17 Sylvia Stuurman
 * @version 1.4 2007/07/16 Sylvia Stuurman
 * @version 1.5 2010/03/03 Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */
public class MenuController extends MenuBar {

	private final SlideViewerComponent slideViewerComponent;
	private Frame parent; //The frame, only used as parent for the Dialogs
	private MenuItem menuItem;
	private Menu fileMenu = new Menu(MenuControlStatic.FILE);
	private Menu viewMenu = new Menu(MenuControlStatic.VIEW);
	private Menu helpMenu = new Menu(MenuControlStatic.HELP);

	public MenuController(Frame frame, SlideViewerComponent slideViewerComponent) {
		parent = frame;
		this.slideViewerComponent = slideViewerComponent;
		add(fileMenu);
		loadPresentation();
		newPresentation();
		savePresentation();
		exitPresentation();
		nextSlide();
		previousSlide();
		moveToSlide();
		about();
		setHelpMenu(helpMenu); //Needed for portability (Motif, etc.).
	}
	public void loadPresentation() {
		fileMenu.add(menuItem = mkMenuItem(MenuControlStatic.OPEN));
		menuItem.addActionListener(actionEvent -> {
			slideViewerComponent.clear();
			AccessorLoad xmlAccessorLoad = new XMLAccessorLoad();
			try {
				xmlAccessorLoad.loadFile(slideViewerComponent.getPresentation(), MenuControlStatic.TESTFILE);
				slideViewerComponent.setSlideNumber(0);
			} catch (IOException exc) {
				JOptionPane.showMessageDialog(parent, MenuControlStatic.IOEX + exc,
						MenuControlStatic.LOADERR, JOptionPane.ERROR_MESSAGE);
			}
			parent.repaint();
		});
	}

	public void newPresentation() {
		fileMenu.add(menuItem = mkMenuItem(MenuControlStatic.NEW));
		menuItem.addActionListener(actionEvent -> {
			slideViewerComponent.clear();
			parent.repaint();
		});
	}

	public void savePresentation() {
		fileMenu.add(menuItem = mkMenuItem(MenuControlStatic.SAVE));
		menuItem.addActionListener(e -> {
			AccessorSave xmlAccessorSave = new XMLAccessorSave();
			try {
				xmlAccessorSave.saveFile(slideViewerComponent.getPresentation(), MenuControlStatic.SAVEFILE);
			} catch (IOException exc) {
				JOptionPane.showMessageDialog(parent, MenuControlStatic.IOEX + exc,
						MenuControlStatic.SAVEERR, JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	public void exitPresentation() {
		fileMenu.addSeparator();
		fileMenu.add(menuItem = mkMenuItem(MenuControlStatic.EXIT));
		menuItem.addActionListener(actionEvent -> slideViewerComponent.getPresentation().exit(0));
	}

	public void nextSlide() {
		//Menu viewMenu = new Menu(MenuControlStatic.VIEW);
		viewMenu.add(menuItem = mkMenuItem(MenuControlStatic.NEXT));
		menuItem.addActionListener(actionEvent -> slideViewerComponent.nextSlide());
	}

	public void previousSlide() {
		viewMenu.add(menuItem = mkMenuItem(MenuControlStatic.PREV));
		menuItem.addActionListener(actionEvent -> slideViewerComponent.prevSlide());
	}

	public void moveToSlide() {
		viewMenu.add(menuItem = mkMenuItem(MenuControlStatic.GOTO));
		menuItem.addActionListener(actionEvent -> {
			String pageNumberStr = JOptionPane.showInputDialog(MenuControlStatic.PAGENR);
			int pageNumber = Integer.parseInt(pageNumberStr);
			if(pageNumber < slideViewerComponent.getPresentation().getSize() + 1){
				slideViewerComponent.setSlideNumber(pageNumber - 1);
			}
		});
	}

	public void about() {
		add(viewMenu);
		Menu helpMenu = new Menu(MenuControlStatic.HELP);
		helpMenu.add(menuItem = mkMenuItem(MenuControlStatic.ABOUT));
		menuItem.addActionListener(actionEvent -> AboutBox.show(parent));
	}

	//Creating a menu-item
	public MenuItem mkMenuItem(String name) {
		return new MenuItem(name, new MenuShortcut(name.charAt(0)));
	}
}
