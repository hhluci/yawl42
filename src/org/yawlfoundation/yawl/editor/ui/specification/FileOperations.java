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

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.specification.pubsub.FileState;
import org.yawlfoundation.yawl.editor.ui.specification.pubsub.FileStateListener;
import org.yawlfoundation.yawl.editor.ui.specification.pubsub.Publisher;
import org.yawlfoundation.yawl.editor.ui.specification.validation.AnalysisResultsParser;
import org.yawlfoundation.yawl.editor.ui.specification.validation.SpecificationValidator;
import org.yawlfoundation.yawl.editor.ui.specification.validation.ValidationResultsParser;
import org.yawlfoundation.yawl.editor.ui.util.CursorUtil;

/**
 * @author Michael Adams
 * @date 26/07/12
 */
public class FileOperations {

    private enum Action { Open, OpenFile, GitParas, Clone, Pull,Commit, Push,PushNewBranch,Validate, Analyse, Save, SaveAs, Close, Exit }


    public static void open()  { processAction(Action.Open); }
    
    public static void gitParas()  { processAction(Action.GitParas); }
    public static void cloneGit()  { processAction(Action.Clone); }
    public static void pull()  { processAction(Action.Pull); }
    public static void commit()  { processAction(Action.Commit); }
    public static void push()  { processAction(Action.Push); }
    public static void pushNewBranch() {processAction(Action.PushNewBranch);}
   
    public static void open(String fileName)  { processAction(Action.OpenFile, fileName); }

    public static void validate()  { processAction(Action.Validate); }

    public static void analyse()  { processAction(Action.Analyse); }

    public static void save() { processAction(Action.Save); }

    public static void saveAs()  { processAction(Action.SaveAs); }

    public static void close()  { processAction(Action.Close); }

    public static void exit()  { processAction(Action.Exit); }


    private static void processAction(Action action, String... args) {
        CursorUtil.showWaitCursor();
        SpecificationFileHandler handler = new SpecificationFileHandler();
        Publisher publisher = Publisher.getInstance();
        publisher.publishFileBusyEvent();

        switch (action) {
            case Open: {
                handler.openFile();
                break;
            }
            case GitParas:{
            	
            	handler.setGitParas();
            	break;
            }
            case Clone:{
            	
            	handler.cloneGit();
            	break;
            }
            case Pull: {

            	handler.pullGit();
            	break;
            }
            case Commit: {

            	handler.commitGit();
            	break;
            }
            case Push: {

            	handler.pushGit();
            	break;
            }
            case PushNewBranch:{
            	handler.pushNewBranchGit();
            	break;
            }
            case OpenFile: {
                handler.openFile(args[0]);
                break;
            }
            case Validate: {
                YAWLEditor.getInstance().showProblemList("Validation Results",
                        new ValidationResultsParser().parse(
                        new SpecificationValidator().getValidationResults()));
                break;
            }
            case Analyse: {
                new AnalysisResultsParser().showAnalysisResults();
                break;
            }
            case Save: {
                handler.saveFile();
                break;
            }
            case SaveAs: {
                handler.saveFileAs();
                break;
            }
            case Close: {
                handler.closeFile();
                break;
            }
            case Exit: {

                // need to listen for file save completion (or file close)
                FileStateListener fsListener = new FileStateListener() {
                    @Override
                    public void specificationFileStateChange(FileState state) {
                        if (state == FileState.Closed) {
                            System.exit(0);
                        }
                    }
                };
                publisher.subscribe(fsListener);

                if (! handler.closeFileOnExit()) {
                    publisher.unsubscribe(fsListener);       // exit cancelled
                }
                break;
            }
        }

        publisher.publishFileUnbusyEvent();
        CursorUtil.showDefaultCursor();
    }

}
