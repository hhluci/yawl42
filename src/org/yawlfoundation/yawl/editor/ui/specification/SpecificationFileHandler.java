/*
 * Copyright (c) 2004-2013 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */
package org.yawlfoundation.yawl.editor.ui.specification;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawl.editor.core.YConnector;
import org.yawlfoundation.yawl.editor.core.YSpecificationHandler;
import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.actions.specification.OpenRecentSubMenu;
import org.yawlfoundation.yawl.editor.ui.plugin.YPluginHandler;
import org.yawlfoundation.yawl.editor.ui.specification.io.SpecificationReader;
import org.yawlfoundation.yawl.editor.ui.specification.io.SpecificationWriter;
import org.yawlfoundation.yawl.editor.ui.specification.pubsub.Publisher;
import org.yawlfoundation.yawl.editor.ui.swing.FileChooserFactory;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.editor.ui.swing.YStatusBar;
import org.yawlfoundation.yawl.editor.ui.util.UserSettings;
import org.yawlfoundation.yawl.util.StringUtil;

public class SpecificationFileHandler {

    private static final String EXTENSION = ".yawl";
    private final YStatusBar _statusBar;
    private final YSpecificationHandler _handler;
    private boolean _closeAfterSave;
    private static Logger logger = LoggerFactory.getLogger(SpecificationFileHandler.class);
    public SpecificationFileHandler() {
        _statusBar = YAWLEditor.getStatusBar();
        _handler = SpecificationModel.getHandler();
    }

    
    public void setGitParas() {
    	GitInfoDialog gitInfoDialog = new GitInfoDialog(YAWLEditor.getInstance(),"Git paras");
		gitInfoDialog.setModal(true);
		gitInfoDialog.setVisible(true);
		GitParas.setUri(gitInfoDialog.getUri());
		GitParas.setName(gitInfoDialog.getName());
		GitParas.setPwd(gitInfoDialog.getPwd());
		GitParas.setLocalPath(gitInfoDialog.getLocalPath());
    }
    /**
     * clone a specification file from Git
     */
	public void cloneGit() {

		if(GitParas.isEmpty()) {
			JOptionPane.showInternalMessageDialog(YAWLEditor.getInstance(), 
					"please first set git paras","information", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		new SwingWorker<StringBuilder, String>() {
			public  void cloneGit() {
		    	try {
		    		String localRepoPath = GitParas.getLocalPath();
		    		String remoteRepoURI = GitParas.getUri();
		    		File localPath = new File(localRepoPath);
					logger.debug("Cloning from " + remoteRepoURI + " to " + localPath);
					publish("Cloning from " + remoteRepoURI + " to " + localPath);
				    Git result = Git.cloneRepository()
				                .setURI(remoteRepoURI)
				                .setDirectory(localPath)
				                .setProgressMonitor(new ProgressMonitor() {
				                    @Override
				                    public void start(int totalTasks) {
				                        logger.debug("Starting work on " + totalTasks + " tasks");
				                        publish("Starting work on " + totalTasks + " tasks");
				                    }

				                    @Override
				                    public void beginTask(String title, int totalWork) {
				                    	logger.debug("Start " + title + ": " + totalWork);
				                    	publish("Start " + title + ": " + totalWork);
				                    }

				                    @Override
				                    public void update(int completed) {
				                    	logger.debug(completed + "-");
				                    	publish(completed + "-");
				                    }

				                    @Override
				                    public void endTask() {
				                    	logger.debug("Done");
				                    	publish("Done");
				                    }

				                    @Override
				                    public boolean isCancelled() {
				                        return false;
				                    }
				                })
				                .call();
					logger.debug("Having repository: " + result.getRepository().getDirectory());
					publish("completed cloning. Having repository:" + result.getRepository().getDirectory());
				    result.close();
						
				}catch (InvalidRemoteException e) {
					logger.error(e.getMessage());
					//e.printStackTrace();
				} catch (TransportException e) {
					//e.printStackTrace();
					logger.error(e.getMessage());
				} catch (GitAPIException e) {
					//e.printStackTrace();
					logger.error(e.getMessage());
				}
				}
			@Override
			protected StringBuilder doInBackground() throws Exception {
				StringBuilder sb = new StringBuilder();
				cloneGit();
				return sb;
			}
			@Override
			protected void process(List<String> chunks) {
				for (String line : chunks) {
					_statusBar.setText(line);
				}
				super.process(chunks);
			}
		}.execute();
		// _statusBar.setText("Cloning from " + GitParas.getUri() + " to " + GitParas.getLocalPath());
		 /*CursorUtil.showWaitCursor();
		 	SwingUtilities.invokeLater(new Runnable() {
             public void run() {
            	 JGitUtil.clone(GitParas.getLocalPath(), GitParas.getUri());
              }
        });
		 	CursorUtil.showDefaultCursor();*/
		// TODO: clone
	}

	public void pushGit() {

		if(GitParas.isEmpty()) {
			JOptionPane.showInternalMessageDialog(YAWLEditor.getInstance(), 
					"please first set git paras","information", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		new SwingWorker<StringBuilder, String>() {
			public  void executeGit() {
		    	try {
		    		String localRepoPath = GitParas.getLocalPath();
		    		String username = GitParas.getName();
		    		String pwd = GitParas.getPwd();
		    		publish("Pushing " + localRepoPath + " to the remote repository.");
		    		File localPath = new File(localRepoPath);   
		    		Git git = Git.open(localPath);
		    		Status status = git.status().call();
		            logger.debug("Modified: " + status.getModified());
		            PushCommand push = git.push();
		            push.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, pwd));
		            push.setProgressMonitor(new ProgressMonitor() {
	                    @Override
	                    public void start(int totalTasks) {
	                        logger.debug("Starting work on " + totalTasks + " tasks");
	                        publish("Starting work on " + totalTasks + " tasks");
	                    }
	                    @Override
	                    public void beginTask(String title, int totalWork) {
	                    	logger.debug("Start " + title + ": " + totalWork);
	                    	publish("Start " + title + ": " + totalWork);
	                    }

	                    @Override
	                    public void update(int completed) {
	                    	logger.debug(completed + "-");
	                    	publish(completed + "-");
	                    }

	                    @Override
	                    public void endTask() {
	                    	logger.debug("Done");
	                    	publish("Done");
	                    }

	                    @Override
	                    public boolean isCancelled() {
	                        return false;
	                    }
	                });
		            Iterable<PushResult> iterable =push.call();
		            PushResult pushResult = iterable.iterator().next();
		            RemoteRefUpdate.Status statu = pushResult.getRemoteUpdate( "refs/heads/master" ).getStatus();
		            logger.debug(statu.toString());
		            publish(statu.toString());
		            git.close();
		            publish("Push done");
				    
						
				}catch (InvalidRemoteException e) {
					logger.error(e.getMessage());
					//e.printStackTrace();
				} catch (TransportException e) {
					//e.printStackTrace();
					logger.error(e.getMessage());
				} catch (GitAPIException e) {
					//e.printStackTrace();
					logger.error(e.getMessage());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					logger.error(e.getMessage());
				}
				}
			@Override
			protected StringBuilder doInBackground() throws Exception {
				StringBuilder sb = new StringBuilder();
				executeGit();
				return sb;
			}
			@Override
			protected void process(List<String> chunks) {
				for (String line : chunks) {
					_statusBar.setText(line);
				}
				super.process(chunks);
			}
		}.execute();
		// TODO: push
	}

	public void pushNewBranchGit() {
		if(GitParas.isEmpty()) {
			JOptionPane.showInternalMessageDialog(YAWLEditor.getInstance(), 
					"please first set git paras","information", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		new SwingWorker<StringBuilder, String>() {
			public  void executeGit() {
		    	try {
		    		String newBranchIndex = "refs/heads/"+GitParas.getName();
		    		String localRepoPath = GitParas.getLocalPath();
		    		String username = GitParas.getName();
		    		String pwd = GitParas.getPwd();
		    		publish("Pushing " + localRepoPath + " to one remote new branch");
		    		File localPath = new File(localRepoPath);   
		    		Git git = Git.open(localPath);
		            List<Ref> refs = git.branchList().call();
		            for (Ref ref : refs) {
		                if (ref.getName().equals(newBranchIndex)) {
		                    logger.debug("Removing branch before");
		                    git.branchDelete().setBranchNames(GitParas.getName()).setForce(true).call();
		                    break;
		                }
		            }            
		            Ref ref = git.branchCreate().setName(GitParas.getName()).call();
		            PushCommand push = git.push();
		            push.add(ref);
		            push.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, pwd));
		            push.setProgressMonitor(new ProgressMonitor() {
	                    @Override
	                    public void start(int totalTasks) {
	                        logger.debug("Starting work on " + totalTasks + " tasks");
	                        publish("Starting work on " + totalTasks + " tasks");
	                    }
	                    @Override
	                    public void beginTask(String title, int totalWork) {
	                    	logger.debug("Start " + title + ": " + totalWork);
	                    	publish("Start " + title + ": " + totalWork);
	                    }

	                    @Override
	                    public void update(int completed) {
	                    	logger.debug(completed + "-");
	                    	publish(completed + "-");
	                    }

	                    @Override
	                    public void endTask() {
	                    	logger.debug("Done");
	                    	publish("Done");
	                    }

	                    @Override
	                    public boolean isCancelled() {
	                        return false;
	                    }
	                });
		            push.call();
		            git.close();
		            publish("Push new Branch done");
				    
						
				}catch (InvalidRemoteException e) {
					logger.error(e.getMessage());
					//e.printStackTrace();
				} catch (TransportException e) {
					//e.printStackTrace();
					logger.error(e.getMessage());
				} catch (GitAPIException e) {
					//e.printStackTrace();
					logger.error(e.getMessage());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					logger.error(e.getMessage());
				}
				}
			@Override
			protected StringBuilder doInBackground() throws Exception {
				StringBuilder sb = new StringBuilder();
				executeGit();
				return sb;
			}
			@Override
			protected void process(List<String> chunks) {
				for (String line : chunks) {
					_statusBar.setText(line);
				}
				super.process(chunks);
			}
		}.execute();
	}
	public void commitGit() {

		if(GitParas.isEmpty()) {
			JOptionPane.showInternalMessageDialog(YAWLEditor.getInstance(), 
					"please first set git paras","information", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		new SwingWorker<StringBuilder, String>() {
			public  void executeGit() {
		    	try {
		    		String localRepoPath = GitParas.getLocalPath();
		    		String username = GitParas.getName();
		    		String pwd = GitParas.getPwd();
		    		publish("Commit...");
		    		File localPath = new File(localRepoPath);   
		    		
		    		Git git = Git.open(localPath);
		    		Status status = git.status().call();
		            logger.debug("Modified: " + status.getModified());
		            publish("Modified: " + status.getModified());
		            AddCommand add = git.add();
		            add.addFilepattern(".");
		            add.call();
		            CommitCommand commits = git.commit();
		            commits.setAuthor(username, "");
		            commits.setMessage("update yawl");
		            commits.call();
		            git.close();  
		            publish("commit done!");
						
				}catch (InvalidRemoteException e) {
					logger.error(e.getMessage());
					//e.printStackTrace();
				} catch (TransportException e) {
					//e.printStackTrace();
					logger.error(e.getMessage());
				} catch (GitAPIException e) {
					//e.printStackTrace();
					logger.error(e.getMessage());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					logger.error(e.getMessage());
				}
				}
			@Override
			protected StringBuilder doInBackground() throws Exception {
				StringBuilder sb = new StringBuilder();
				executeGit();
				return sb;
			}
			@Override
			protected void process(List<String> chunks) {
				for (String line : chunks) {
					_statusBar.setText(line);
				}
				super.process(chunks);
			}
		}.execute();
		// TODO: commit
	}

	public void pullGit() {

		if(GitParas.isEmpty()) {
			JOptionPane.showInternalMessageDialog(YAWLEditor.getInstance(), 
					"please first set git paras","information", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		new SwingWorker<StringBuilder, String>() {
			public  void executeGit() {
		    	try {
		    		String localRepoPath = GitParas.getLocalPath();
		    		String username = GitParas.getName();
		    		String pwd = GitParas.getPwd();
		    		publish("Pull...");
		    		File localPath = new File(localRepoPath);   
		    		Git git = Git.open(localPath);
		    		
		           
		            PullCommand pullCmd = git.pull();
		           
		    		pullCmd.setProgressMonitor(new ProgressMonitor() {
	                    @Override
	                    public void start(int totalTasks) {
	                        logger.debug("Starting work on " + totalTasks + " tasks");
	                        publish("Starting work on " + totalTasks + " tasks");
	                    }
	                    @Override
	                    public void beginTask(String title, int totalWork) {
	                    	logger.debug("Start " + title + ": " + totalWork);
	                    	publish("Start " + title + ": " + totalWork);
	                    }

	                    @Override
	                    public void update(int completed) {
	                    	logger.debug(completed + "-");
	                    	publish(completed + "-");
	                    }

	                    @Override
	                    public void endTask() {
	                    	logger.debug("Done");
	                    	publish("Done");
	                    }

	                    @Override
	                    public boolean isCancelled() {
	                        return false;
	                    }
	                });
		    		 	PullResult result = pullCmd.call();
			            FetchResult fetchResult = result.getFetchResult();
			            MergeResult mergeResult = result.getMergeResult();
			            mergeResult.getMergeStatus();  // this should be interesting
			        	
			            logger.debug(mergeResult.getMergeStatus().toString());
			            logger.debug(fetchResult.getMessages());
			            git.close();
				        publish("pull done!");
						
				}catch (InvalidRemoteException e) {
					logger.error(e.getMessage());
					//e.printStackTrace();
				} catch (TransportException e) {
					//e.printStackTrace();
					logger.error(e.getMessage());
				} catch (GitAPIException e) {
					//e.printStackTrace();
					logger.error(e.getMessage());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					logger.error(e.getMessage());
				}
				}
			@Override
			protected StringBuilder doInBackground() throws Exception {
				StringBuilder sb = new StringBuilder();
				executeGit();
				return sb;
			}
			@Override
			protected void process(List<String> chunks) {
				for (String line : chunks) {
					_statusBar.setText(line);
				}
				super.process(chunks);
			}
		}.execute();
		// TODO: pull
	}
    /**
     * Prompts the user for and opens a specification file
     */
    public void openFile() {
        openFile(promptForLoadFileName());
    }

    /**
     * Opens a specification file
     * @param fileName the name of the file to open
     */
    public void openFile(String fileName) {
        loadFromFile(fileName);
    }

    /**
     *  Saves the currently open specification. This might include prompting for a file
     *  name if one has not yet been set for the specification.
     *  @return true if the specification was saved, false if the user cancelled the save.
     */
    public boolean saveFile() {
        String fileName = getFileName();
        if (StringUtil.isNullOrEmpty(fileName) || ! fileName.endsWith(EXTENSION)) {
            fileName = promptForSaveFileName(SaveMode.Save);
            if (fileName == null) {
                return false;
            }
        }
        saveSpecification(fileName);
        return true;
    }

    /**
     *  Saves the currently open specification to a new file.
     */
    public void saveFileAs() {
        String fileName = promptForSaveFileName(SaveMode.SaveAs);
        if (fileName != null) {
            saveSpecification(fileName);
        }
    }

    /**
     *  Processes a user's request to close an open specification.
     *  This might include prompting for a file name and saving
     *  the specification before closing it.
     */
    public void closeFile() {
        _statusBar.setText("Closing Specification...");
        handleCloseResponse();
    }

    /**
     *  Processes a user's request to exit the application.
     *  This might include prompting for a file name and saving
     *  the specification before closing it.
     */
    public boolean closeFileOnExit() {
        _statusBar.setText("Exiting YAWL Editor...");
        YConnector.disconnect();
        return Publisher.getInstance().isFileStateClosed() || handleCloseResponse();
    }

    /****************************************************************************/

    private String getFileName() { return _handler.getFileName(); }

    /**
     * Asks user if they want to save the open specification before exiting
     * @return true if ok to close, false if user cancelled exiting
     */
    private boolean handleCloseResponse() {

        // only prompt to save if there have been changes
        if (SpecificationUndoManager.getInstance().isDirty()) {
            int response = getSaveOnCloseConfirmation();
            if (response == JOptionPane.CANCEL_OPTION) {         // user cancelled exit
                _statusBar.setTextToPrevious();
                return false;
            }
            if (response == JOptionPane.YES_OPTION) {           // save then exit
                return saveWhilstClosing();
            }
        }
        closeWithoutSaving();        // NO_OPTION               // exit and don't save
        return true;
    }

    /**
     * Constructs a suggested name for a new file, from the last used path and the
     * specification URI
     * @return the suggested file name to save the specification to
     */
    private File getSuggestedFileName() {
        String fileName = _handler.getFileName();
        if (fileName != null) {
            return new File(fileName);
        }

        // no existing filename, so build one from last known path and spec uri
        String path = UserSettings.getLastSaveOrLoadPath();
        if (path == null) {
            path = System.getProperty("user.home");
        }
        else if (! path.endsWith(File.separator)) {
            path = path.substring(0, path.lastIndexOf(File.separator));
        }
        return new File(path, _handler.getID().getUri() + EXTENSION);
    }

    private String promptForSaveFileName(SaveMode mode) {
        JFileChooser dialog = FileChooserFactory.build(EXTENSION, "YAWL Specification",
                        mode.dialogTitle);

        dialog.setSelectedFile(getSuggestedFileName());
        int response = dialog.showDialog(YAWLEditor.getInstance(), mode.buttonText);
        if (response == JFileChooser.CANCEL_OPTION) {
            return null;
        }

        File file = dialog.getSelectedFile();
        if (! validateFileName(file.getAbsoluteFile())) {
            return promptForSaveFileName(mode);      // try again
        }

        // make sure the selected file name has the correct '.yawl' extension
        if (! file.getName().endsWith(EXTENSION)) {
            file = new File(file.getParentFile(), file.getName() + EXTENSION);
        }

        if (matchesExistingFile(file)) {
            response = JOptionPane.showConfirmDialog(YAWLEditor.getInstance(),
                    "You have selected an existing specification file.\n" +
                    "Are you sure you want to overwrite the existing file?",
                    "Existing File Selected",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (response == JOptionPane.NO_OPTION) return null;
        }
        return file.getAbsolutePath();
    }

    private boolean matchesExistingFile(File file) {
        return file.exists() && ! file.getAbsolutePath().equals(getFileName());
    }

    private void saveSpecification(String fileName) {
        if (StringUtil.isNullOrEmpty(fileName)) return;

        YPluginHandler.getInstance().preSaveFile(fileName);
        saveToFile(fileName);
    }

    private boolean validateFileName(File file) {
        if (file.exists()) {
            return true;
        }
        try {
            if (file.createNewFile()) {
                file.delete();
            }
            return true;
        }
        catch (IOException ioe) {
            MessageDialog.error("Invalid file path: " + file.getAbsolutePath(),
                    "File Error");
            return false;
        }
    }

    private int getSaveOnCloseConfirmation() {
        return JOptionPane.showConfirmDialog(
                YAWLEditor.getInstance(),
                "Do you wish to save your changes before closing?   \n\n"
                        + "\t\t\t'Yes' to save the specification,\n"
                        + "\t\t\t'No' to discard unsaved changes,\n"
                        + "\t\t\t'Cancel' to continue editing.\n\n",
                "Save changes?",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
    }

    private void doPreSaveClosingWork() {
        YAWLEditor.getNetsPane().setVisible(false);
        YAWLEditor.getPropertySheet().setIgnoreRepaint(true);
    }

    private void doPostSaveClosingWork() {
        YAWLEditor.getNetsPane().closeAllNets();
        Publisher.getInstance().publishCloseFileEvent();
        SpecificationModel.reset();
        YPluginHandler.getInstance().specificationClosed();
        SpecificationUndoManager.getInstance().discardAllEdits();
        YAWLEditor.getNetsPane().setVisible(true);
    }

    private boolean saveWhilstClosing() {
        String fileName = getFileName();
        if (StringUtil.isNullOrEmpty(fileName) || ! fileName.endsWith(EXTENSION)) {
            fileName = promptForSaveFileName(SaveMode.Save);
            if (fileName == null) {
                return false;
            }
        }

        _closeAfterSave = true;
        doPreSaveClosingWork();
        saveSpecification(fileName);

        return true;
    }

    private void closeWithoutSaving() {
        doPreSaveClosingWork();
        doPostSaveClosingWork();
    }

    private void saveToFile(String fileName) {
        if (StringUtil.isNullOrEmpty(fileName)) {

            // rollback version number if auto-incrementing
            if (UserSettings.getAutoIncrementVersionOnSave()) {
                _handler.getVersion().minorRollback();
            }
            return;     // user-cancelled save or no file name selected
        }

        _statusBar.setText("Saving Specification...");
        _statusBar.progressOverSeconds(2);

        // SpecificationWriter is a SwingWorker
        SpecificationWriter writer = new SpecificationWriter(fileName);
        writer.addPropertyChangeListener(new SaveCompletionListener(fileName));
        writer.execute();
    }

    private void loadFromFile(String fullFileName) {
        if (fullFileName == null) return;
        _statusBar.setText("Opening Specification...");
        _statusBar.progressOverSeconds(4);
        YAWLEditor.getNetsPane().setVisible(false);

        // SpecificationReader is a SwingWorker
        SpecificationReader reader = new SpecificationReader(fullFileName);
        reader.addPropertyChangeListener(new LoadCompletionListener(fullFileName));
        reader.execute();
    }

    private String promptForLoadFileName() {
        JFileChooser chooser = FileChooserFactory.build(EXTENSION,
                "YAWL Specification", "Open specification");
        int response = chooser.showDialog(YAWLEditor.getInstance(), "Open");
        if (response == JFileChooser.CANCEL_OPTION) return null;

        File file = chooser.getSelectedFile();

        // check for odd dirs on non dos os's
        return file.isFile() ? file.getAbsolutePath() : null;
    }


    enum SaveMode {
        Save("Save Specification", "Save"),
        SaveAs("Save Specification As", "Save As");

        String dialogTitle;
        String buttonText;

        SaveMode(String title, String btnText) {
            dialogTitle = title;
            buttonText = btnText;
        }
    }

    /***************************************************************************/

    // listens for reader swing worker completion & does final cleanups
    class LoadCompletionListener implements PropertyChangeListener {

        String fullFileName;

        LoadCompletionListener(String fileName) { fullFileName = fileName; }

        public void propertyChange(PropertyChangeEvent event) {
            if (event.getNewValue() == SwingWorker.StateValue.DONE) {
                YAWLEditor.getNetsPane().setVisible(true);
                _statusBar.resetProgress();
                OpenRecentSubMenu.getInstance().addRecentFile(fullFileName);
                YPluginHandler.getInstance().specificationLoaded();
            }
        }
    }


    // listens for writer swing worker completion & does final cleanups
    class SaveCompletionListener implements PropertyChangeListener {

        String fullFileName;

        SaveCompletionListener(String fileName) { fullFileName = fileName; }

        public void propertyChange(PropertyChangeEvent event) {
            if (event.getNewValue() == SwingWorker.StateValue.DONE) {
                SpecificationWriter writer = (SpecificationWriter) event.getSource();
                if (writer.successful()) {
                    SpecificationUndoManager.getInstance().setDirty(false);
                    _statusBar.setText("Saved to file: " + fullFileName);
                    OpenRecentSubMenu.getInstance().addRecentFile(fullFileName);
                    YPluginHandler.getInstance().postSaveFile();
                    if (_closeAfterSave) {
                        doPostSaveClosingWork();
                        YAWLEditor.getInstance().setTitle("");
                    }
                    else {
                        YAWLEditor.getInstance().setTitle(fullFileName);
                    }
                }
                else _statusBar.setTextToPrevious();

                _statusBar.resetProgress();
                _closeAfterSave = false;
            }
        }
    }


}
