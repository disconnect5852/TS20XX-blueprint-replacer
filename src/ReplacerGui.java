import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.jdesktop.swingx.JXTable;



public class ReplacerGui extends JFrame {
	private static final long serialVersionUID = 5071717594809194045L;
	private static List<RWXml> xmls= new ArrayList<RWXml>();
	//private static final ImageIcon icon = new ImageIcon("problem.png");
	private static final JList<String> filterlist= new JList<String>();
	private static JTree tree= new JTree(new DefaultMutableTreeNode());
	private static final Color ALTCOLOR=new Color(0.8f,0.9f,1f);
	private static JFileChooser filechoose;
	//private static String defaultlocation=System.getProperty("user.dir");
	private static final Properties props = new Properties(); 
	private static final String PROPLOC="config.cfg";
	private static final String CACHELOC="filetree.cache";
	private static final String DEF_KEY="default_dir";
	private static final String ASSETS_KEY="assets_path";
	private static final String CONFIG_HEADER="Replacer config file";
	//private static final Date dt= new Date(1371057093203l);
	private static final String PATH_SEP=System.getProperty("file.separator");
	private static final JXTable replacelist=new JXTable() {
	/**
		 * 
		 */
		private static final long serialVersionUID = -4560886140968440706L;

		//private static final long serialVersionUID = 8981101312802784431L;
		//alternating row colors
		public Component prepareRenderer(TableCellRenderer renderer,int rowIndex, int vColIndex) {
				Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
				if (isRowSelected(rowIndex)) {
					c.setBackground(Color.green);
				} else if (rowIndex % 2 == 0 /*&& !isCellSelected(rowIndex, vColIndex)*/) {
					c.setBackground(ALTCOLOR);
				} else {
					c.setBackground(getBackground());
				}
				return c;
		}
		
	};
	private static final int THREADCOUNT=Runtime.getRuntime().availableProcessors()*2;
	private static final String VERSION="1.35d";
	private static final String FRAMETEXT="Railworks Replacer by Disc (working threads="+THREADCOUNT+") Version "+VERSION;
	private static final String FRAMETEXTWORK=FRAMETEXT+" (working)";
	
	private static DefaultMutableTreeNode createTree(String dirname) {
			File f = new File(dirname);
			DefaultMutableTreeNode top = new DefaultMutableTreeNode();

			top.setUserObject(f.getName());
			if (f.isDirectory() ) {
			  //System.out.println("Processing Directory " + f);
			  File fls[] = f.listFiles(new FileFilter() {
					
					@Override
					public boolean accept(File pathname) {
						if (pathname.isDirectory()) return true;
						return pathname.getAbsolutePath().endsWith(".bin");
					}
				});
			  for (int i=0;i<fls.length;i++) {
				top.insert(createTree(fls[i].getPath()),i);
			  }
			}
			return(top);
	}
	/*private static DefaultMutableTreeNode createTree(String dirname) throws IOException {
		Path f = FileSystems.getDefault().getPath(dirname);
		DefaultMutableTreeNode top = new DefaultMutableTreeNode();

		top.setUserObject(f.getFileName());
		if (Files.isDirectory(f)) {
		  System.out.println("Processing Directory " + f);
			DirectoryStream<Path> stream = Files.newDirectoryStream(f, ".");
			int i=0;
		  for (Path file: stream) {
			top.insert(createTree(file.toString()),i);
			System.out.println("file:"+f);
			i++;
		  }
		}
		return(top);
	}*/
	
	private void initTree() {
		ReplacerGui.this.setTitle(FRAMETEXTWORK);
		String assetspath= props.getProperty(ASSETS_KEY);
		if (assetspath!=null && assetspath.toLowerCase().endsWith("assets")) {
			File cache= new File(CACHELOC);
			try {
				if (cache.exists()) {
					ObjectInputStream cacheread= new ObjectInputStream(new FileInputStream(cache));
					Object obj= cacheread.readObject();
					if (obj instanceof JTree) {
						tree=(JTree) obj;
						cacheread.close();
					} else  {
						JOptionPane.showMessageDialog(ReplacerGui.this, "Filetree cache is corrupted, and deleted please refresh it or restart this application", "Warning", JOptionPane.WARNING_MESSAGE);
						cacheread.close();
						cache.delete();
					}
				}
				else {
					tree.setModel(new DefaultTreeModel(createTree(assetspath)));
		
					//tree.updateUI();
					//System.out.println(assetspath);
					ObjectOutputStream cachewrite= new ObjectOutputStream(new FileOutputStream(cache));
					cachewrite.writeObject(tree);
					cachewrite.close();
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(ReplacerGui.this, "Something bad happened when reading or writing file tree cache", "Problem?", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(ReplacerGui.this, "Assets directory is not set/or not correct directory selected, please set it from Assets menu", "Warning", JOptionPane.WARNING_MESSAGE);
		}
		ReplacerGui.this.setTitle(FRAMETEXT);
	}
	
	public ReplacerGui() {
		super(FRAMETEXT);
		/*if (Calendar.getInstance().getTime().after(dt)) {
			JOptionPane.showMessageDialog(ReplacerGui.this, "This application is outdated, please download a newer one", "Outdated", JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
		}*/
		if (!new File("serz.exe").exists()) {
			JOptionPane.showMessageDialog(ReplacerGui.this, "Serz.exe isn't detected in same directory as this application! Without it you won't be able to open bin files.", "Problem?", JOptionPane.WARNING_MESSAGE);
		}
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			File propfil= new File(PROPLOC);
			if (!propfil.exists()) {
				props.put(DEF_KEY, System.getProperty("user.dir"));
					props.store(new FileOutputStream(PROPLOC),CONFIG_HEADER);
			}
			props.load(new FileInputStream(propfil));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(ReplacerGui.this, e.getMessage(), "Problem?", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		initTree();
		VersionCheck ver= new VersionCheck(this, VERSION);
		ver.start();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		toolkit.getScreenSize();
		this.setSize(toolkit.getScreenSize().width/2, toolkit.getScreenSize().height/2 );
		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		JMenuBar menubar  = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu tableMenu = new JMenu("Table");
		JMenuItem defaultloc = new JMenuItem("Set Default location");
		JMenuItem openItem = new JMenuItem("Open file(s)...");
		JMenuItem closeItem = new JMenuItem("Close all files...");
		final JMenuItem openTable = new JMenuItem("Open table...");
		final JMenuItem saveTable = new JMenuItem("Save table...");
		saveTable.setEnabled(false);
		openTable.setEnabled(false);
		openTable.setArmed(false);
		saveTable.setArmed(false);
		//openTable.setEnabled(false);
		JPanel leftpanel = new JPanel();
		leftpanel.setPreferredSize(new Dimension(this.getWidth()/4,this.getHeight()/4));
		leftpanel.setLayout(new BorderLayout());
		//leftpanel.setBackground(Color.blue);
		JButton filter= new JButton("Filter");
		JPanel centerpanel = new JPanel();
		centerpanel.setLayout(new BorderLayout());
		//centerpanel.setBackground(Color.red);
		//final JProgressBar progress= new JProgressBar(0,100);
		//progress.setString("Working...");
		JButton execute= new JButton("Execute");
		replacelist.setColumnControlVisible(true);
		replacelist.setShowGrid(false, true);
		replacelist.setRowSelectionAllowed(true);
		replacelist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		centerpanel.add(new JScrollPane(replacelist), BorderLayout.CENTER);
		centerpanel.add(execute, BorderLayout.SOUTH);
		final JMenuItem setAssets= new JMenuItem("Set Assets Dir");
		final JMenuItem refreshTree= new JMenuItem("Refresh Filetree");
		final JMenuItem switchshadowCasting= new JMenuItem("Toggle shadow casting of lights in selected file/directory");
		final JMenuItem enableshadowCasting= new JMenuItem("Enable shadow casting of lights in selected file/directory");
		final JMenuItem disableshadowCasting= new JMenuItem("Disable shadow casting of lights in selected file/directory");
		JMenu assetsMenu= new JMenu("Assets");
		assetsMenu.add(setAssets);
		assetsMenu.add(refreshTree);
		//assetsMenu.add(switchshadowCasting);
		final JPopupMenu browsepopup= new JPopupMenu();
		browsepopup.add(switchshadowCasting);
		browsepopup.add(enableshadowCasting);
		browsepopup.add(disableshadowCasting);
		fileMenu.add(openItem);
		fileMenu.add(defaultloc);
		fileMenu.add(closeItem);
		tableMenu.add(openTable);
		tableMenu.add(saveTable);
		menubar.add(fileMenu);
		menubar.add(tableMenu);
		menubar.add(assetsMenu);
		
		JMenuItem mntmDecodeDavFiles = new JMenuItem("Decode DAV files");
		final JFileChooser davchoose= new JFileChooser(props.getProperty(DEF_KEY));
		davchoose.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		davchoose.setMultiSelectionEnabled(true);
		davchoose.setName("Select the dav files/directories where you want to decode davs");
		davchoose.setFileFilter(new FileNameExtensionFilter("Railworks/RS dav file (.dav)","dav"));
		final JFileChooser davoutput=new JFileChooser(props.getProperty(DEF_KEY));
		davoutput.setMultiSelectionEnabled(false);
		davoutput.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		davoutput.setName("Choose an output directory for wav files");
		mntmDecodeDavFiles.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				Path outputpath=null;
				if(davchoose.showOpenDialog(ReplacerGui.this)==JFileChooser.APPROVE_OPTION) {
					if (JOptionPane.showConfirmDialog(ReplacerGui.this,"Do you want to choose output directory? \n (Otherwise the files will be converted to the same directory where the original is)", "Do you want to choose output directory?", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
						if (davoutput.showSaveDialog(ReplacerGui.this)==JFileChooser.APPROVE_OPTION) {
							outputpath=davoutput.getSelectedFile().toPath();
						}
					}
					Set<Path> paths=new HashSet<Path>();
					for(File fil:davchoose.getSelectedFiles()) {
						paths.add(fil.toPath());
					}
					try {
						RWUtils.decodeDavs(outputpath, paths);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(ReplacerGui.this, e.getMessage(), "Problem?", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			}
		});
		assetsMenu.add(mntmDecodeDavFiles);
		
		JMenuItem mntmZipFilesTo = new JMenuItem("Zip files to RWP");
		final JFileChooser zipchoose= new JFileChooser(props.getProperty(DEF_KEY));
		zipchoose.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		zipchoose.setMultiSelectionEnabled(true);
		zipchoose.setName("Select the zip files/directories that containing zips which you want to convert");
		zipchoose.setFileFilter(new FileNameExtensionFilter("Zip files with railworks assets, or a directory that have these (.zip)","zip"));
		final JFileChooser zipoutput=new JFileChooser(props.getProperty(DEF_KEY));
		zipoutput.setMultiSelectionEnabled(false);
		zipoutput.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		zipoutput.setName("Choose an output directory for rwp files");
		mntmZipFilesTo.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				Path outputpath=null;
				if(zipchoose.showOpenDialog(ReplacerGui.this)==JFileChooser.APPROVE_OPTION) {
					if (JOptionPane.showConfirmDialog(ReplacerGui.this,"Do you want to choose output directory? \n (Otherwise the files will be converted to the same directory where the original is)", "Do you want to choose output directory?", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
						if (zipoutput.showSaveDialog(ReplacerGui.this)==JFileChooser.APPROVE_OPTION) {
							outputpath=zipoutput.getSelectedFile().toPath();
						}
					}
					Set<Path> paths=new HashSet<Path>();
					for(File fil:zipchoose.getSelectedFiles()) {
						paths.add(fil.toPath());
					}
					try {
						ReplacerGui.this.setTitle(FRAMETEXTWORK);
						RWUtils.zipsToRwp(outputpath, paths);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(ReplacerGui.this, e.getMessage(), "Problem?", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
					ReplacerGui.this.setTitle(FRAMETEXT);
				}
			}
		});
		assetsMenu.add(mntmZipFilesTo);
		JMenu tracksMenu= new JMenu("Tracks");
		final JMenuItem trackHeightOffset= new JMenuItem("Offset track height");
		trackHeightOffset.setToolTipText("Offset track height (if there any tracks.bin/xml is opened)");
		tracksMenu.add(trackHeightOffset);
		menubar.add(tracksMenu);
		this.setJMenuBar(menubar);
		//JScrollPane filterscrollpane= new JScrollPane();
		//filterscrollpane.add(filterlist);
		//leftpanel.add(progress, BorderLayout.NORTH);
		//leftpanel.add(new JScrollPane(filterlist), BorderLayout.CENTER);
		leftpanel.add(filter, BorderLayout.SOUTH);
		//leftpanel.add(new JScrollPane(tree), BorderLayout.NORTH);
		JSplitPane leftsplit= new JSplitPane(JSplitPane.VERTICAL_SPLIT,new JScrollPane(tree), new JScrollPane(filterlist));
		leftsplit.setResizeWeight(0.7);
		leftpanel.add(leftsplit, BorderLayout.CENTER);
		//this.add(leftpanel, BorderLayout.WEST);
		//this.add(centerpanel, BorderLayout.CENTER );
		JSplitPane fullpane= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftpanel, centerpanel);
		fullpane.setResizeWeight(0.1);
		getContentPane().add(fullpane);
		filechoose= new JFileChooser(props.getProperty(DEF_KEY));
		// offset dialog¡¡
		final JDialog dialog= new JDialog(ReplacerGui.this,"Set Track offset");
		dialog.setModal(true);
		dialog.getContentPane().setLayout(new FlowLayout());
		final JLabel setoffset= new JLabel("Valid");
		final JTextField offsetfield= new JTextField("0.0",5);
		offsetfield.setToolTipText("You can use negative numbers too");
		offsetfield.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased(KeyEvent ke) {
                String typed = offsetfield.getText();
                if(!typed.matches("(-)?+\\d+(\\.\\d*)?")) {
                	setoffset.setText("Invalid");
                    return;
                }
                RWXml.setTrackHeightOffset(Double.parseDouble(typed));
                setoffset.setText("Valid");
            }
        });
		dialog.getContentPane().add(offsetfield);
		dialog.getContentPane().add(setoffset);
		dialog.pack();
		//offset dialog end
		
		final FileNameExtensionFilter routefile= new FileNameExtensionFilter("Railworks route file (.xml; .bin)","xml","bin");
		final FileNameExtensionFilter xmlobj= new FileNameExtensionFilter("Replacer xml object (.xml)","xml");
		
		trackHeightOffset.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				dialog.setVisible(true);
			}
		});
		
		setAssets.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				JFileChooser fcs= new JFileChooser(props.getProperty(ASSETS_KEY,System.getProperty("user.dir")));
				fcs.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fcs.setMultiSelectionEnabled(false);
				fcs.setName("Select the Assets directory");
				if (fcs.showOpenDialog(ReplacerGui.this)==JFileChooser.APPROVE_OPTION) {
					String selectedloc=fcs.getSelectedFile().getPath();
					props.put(ASSETS_KEY, selectedloc);
					try {
						props.store(new FileOutputStream(PROPLOC),CONFIG_HEADER);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(ReplacerGui.this, e.getMessage(), "Problem?", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
					new File(CACHELOC).delete();
					initTree();
				}
			}
		});
		refreshTree.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				new File(CACHELOC).delete();
				initTree();
			}
		});
		tree.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 2 && !e.isConsumed()) {
					e.consume();
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					if (node == null || !node.getUserObject().toString().endsWith(".bin")) return;
					TreeNode[] tree= node.getPath();
					if (tree[0].toString().equalsIgnoreCase("assets")) {
						int[] selectedRows = replacelist.getSelectedRows();
						if (selectedRows.length!=0) {
							StringBuilder blueprintID= new StringBuilder();
							for (int i=3; i<tree.length-1; i++) {
								blueprintID.append(tree[i]);
								blueprintID.append(PATH_SEP);
							}
							blueprintID.append(tree[tree.length-1]);
							String blueprint = blueprintID.toString();
							blueprint= blueprint.replace(".bin", ".xml");
							blueprint= blueprint.replace(".BIN", ".xml");
							TableModel model= replacelist.getModel();
							if (model instanceof RWTableModel) {
								RWTableModel rwmodel=(RWTableModel) model;
								for (int row:selectedRows) {
									rwmodel.setValuesAtRow(tree[1].toString(), tree[2].toString(), blueprint, replacelist.convertRowIndexToModel( row));
								}
							}
							
						}
					}
				}
				else if (e.getButton()==MouseEvent.BUTTON3) {
					e.consume();
					browsepopup.show(e.getComponent(),e.getX(), e.getY());
				}
			}
		});
		openItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
				filechoose.setMultiSelectionEnabled(true);
				filechoose.removeChoosableFileFilter(xmlobj);
				filechoose.setFileFilter(routefile);
				if (filechoose.showOpenDialog(ReplacerGui.this)==JFileChooser.APPROVE_OPTION) {
						ReplacerGui.this.setTitle(FRAMETEXTWORK);
						xmls= new ArrayList<RWXml>();
						File[] files= filechoose.getSelectedFiles();
						//progress.setMaximum(files.length);
						ExecutorService execSvc = Executors.newFixedThreadPool( THREADCOUNT );
						for (int i=0; i<files.length; i++) {
							try {
								RWXml xml= new RWXml(files[i].getAbsolutePath());
								execSvc.execute(xml);
								//xml.start();
								if (xml!=null) {
									//xml.populateReplaceables();
									xmls.add(xml);
								}
							} catch (Exception e) {
								JOptionPane.showMessageDialog(ReplacerGui.this, e.getMessage(), "Problem?", JOptionPane.ERROR_MESSAGE);
								e.printStackTrace();
								System.out.println("exception");
							}
							//progress.setValue(i+1);
						}
						execSvc.shutdown();
						try {
							while (!execSvc.awaitTermination(10, TimeUnit.MINUTES )) {
								//System.out.println("sleep");
								Thread.sleep(1000);

							}
						} catch (InterruptedException e) {
								e.printStackTrace();
						}

						filterlist.setListData(RWXml.getTypeList());
						ReplacerGui.this.setTitle(FRAMETEXT);
						openTable.setEnabled(true);
				}
            }
        });
		filter.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				ReplacerGui.this.setTitle(FRAMETEXTWORK);
				//RWXml.clearReplaceables();
				RWXml.setSelectedTypeList(filterlist.getSelectedValuesList());
				//progress.setMaximum(xmls.size());
				ExecutorService execSvc = Executors.newFixedThreadPool( THREADCOUNT );
				for (int i=0; i<xmls.size(); i++) {
					RWXml xml= xmls.get(i);
					execSvc.execute(xml.populateReplaceables);
					//progress.setValue(i+1);
				}
				execSvc.shutdown();
				try {
					while (!execSvc.awaitTermination(10, TimeUnit.MINUTES )) {
						
						Thread.sleep(500);

					}
				} catch (InterruptedException e) {
						e.printStackTrace();
				}
				replacelist.setModel(new RWTableModel(RWXml.getReplace()));
				replacelist.packAll();
				saveTable.setEnabled(true);
				ReplacerGui.this.setTitle(FRAMETEXT);
			}
		});
		execute.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				ReplacerGui.this.setTitle(FRAMETEXTWORK);
				//progress.setMaximum(xmls.size());
				ExecutorService execSvc = Executors.newFixedThreadPool( THREADCOUNT );
				for (int i=0; i<xmls.size(); i++) {
					execSvc.execute(xmls.get(i).replaceAll);
					//progress.setValue(i+1);
				}
				execSvc.shutdown();
				try {
					while (!execSvc.awaitTermination(10, TimeUnit.MINUTES )) {
						
						Thread.sleep(500);

					}
				} catch (InterruptedException e) {
						e.printStackTrace();
				}
				offsetfield.setText("0.0");
				ReplacerGui.this.setTitle(FRAMETEXT);
			}
		});
		saveTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				if (saveTable.isEnabled()) {
				filechoose.setMultiSelectionEnabled(false);
				filechoose.removeChoosableFileFilter(routefile);
				filechoose.setFileFilter(xmlobj);
				try {
					if (filechoose.showSaveDialog(ReplacerGui.this)==JFileChooser.APPROVE_OPTION) {
						ReplacerGui.this.setTitle(FRAMETEXTWORK);
						RWXml.writeTable(filechoose.getSelectedFile());
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(ReplacerGui.this, e.getStackTrace(), "Problem?", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
				ReplacerGui.this.setTitle(FRAMETEXT);
			}
			}
		});
		openTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				if (openTable.isEnabled()) {
				filechoose.setMultiSelectionEnabled(false);
				filechoose.removeChoosableFileFilter(routefile);
				filechoose.setFileFilter(xmlobj);
				try {
					if (filechoose.showOpenDialog(ReplacerGui.this)==JFileChooser.APPROVE_OPTION) {
						ReplacerGui.this.setTitle(FRAMETEXTWORK);
						/*XMLEncoder encoder= new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filechoose.getSelectedFile())));
						List<Replaceable> list= RWXml.getReplace();
						for (int i=0; i<list.size(); i++) {
							encoder.writeObject(list.get(i));
						}
						encoder.close();*/
						RWXml.readTable(filechoose.getSelectedFile());
						replacelist.setModel(new RWTableModel(RWXml.getReplace()));
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(ReplacerGui.this, e.getStackTrace(), "Problem?", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
				ReplacerGui.this.setTitle(FRAMETEXT);
				saveTable.setEnabled(true);
				}
			}
		});
		defaultloc.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				JFileChooser fcs= new JFileChooser();
				fcs.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fcs.setMultiSelectionEnabled(false);
				if (fcs.showOpenDialog(ReplacerGui.this)==JFileChooser.APPROVE_OPTION) {
					String selectedloc=fcs.getSelectedFile().getPath();
					props.put(DEF_KEY, selectedloc);
					try {
						props.store(new FileOutputStream(PROPLOC),CONFIG_HEADER);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(ReplacerGui.this, e.getMessage(), "Problem?", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			}
		});
		switchshadowCasting.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				changelights(2);
			}
		});
		enableshadowCasting.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				changelights(1);
			}
		});
		disableshadowCasting.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				changelights(0);
			}
		});
		closeItem.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				xmls.clear();
				filterlist.removeAll();
				Object model= replacelist.getModel();
				if (model instanceof RWTableModel) {
					((RWTableModel) model).clearData();
				}
				openTable.setEnabled(false);
				saveTable.setEnabled(false);
				RWXml.clearReplaceables();
				System.runFinalization();
				System.gc();
			}
		});
		setVisible(true);
		
	}
	
	private void changelights(int state) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		TreeNode[] treenodes= node.getPath();
		StringBuilder path=new StringBuilder();
		path.append(PATH_SEP);
		for (int i=1; i<treenodes.length-1; i++) {
			path.append(treenodes[i].toString());
			path.append(PATH_SEP);
		}
		path.append(treenodes[treenodes.length-1]);
		if (path!=null) {
			ReplacerGui.this.setTitle(FRAMETEXTWORK);
			Path fil= FileSystems.getDefault().getPath((props.getProperty(ASSETS_KEY)+path.toString()));
			if (Files.exists(fil)) {

				if (Files.isDirectory(fil)) {
					ExecutorService execSvc = Executors.newFixedThreadPool( THREADCOUNT );
					DirectoryStream<Path> stream=null;
					try {
						stream = Files.newDirectoryStream(fil, "*.bin");
					} catch (IOException e1) {
						e1.printStackTrace();
						return;
					}
					for (Path file: stream) {
						RWXml xml= new RWXml(file.toString(),state);
						execSvc.execute(xml);
					}

					execSvc.shutdown();
					try {
						while (!execSvc.awaitTermination(10, TimeUnit.MINUTES )) {
							Thread.sleep(500);

						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					new RWXml(fil.toString(),state).start();
				}
				ReplacerGui.this.setTitle(FRAMETEXT);
			}
			else JOptionPane.showMessageDialog(ReplacerGui.this, "The file/directory you've selected isn't exists. Please use Refresh Filetree option to refresh the tree contents!", "Problem?", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	public static void main(String[] args) {
		new ReplacerGui();

	}
}
