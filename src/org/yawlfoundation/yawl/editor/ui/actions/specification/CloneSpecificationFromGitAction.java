package org.yawlfoundation.yawl.editor.ui.actions.specification;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.yawlfoundation.yawl.editor.ui.specification.FileOperations;
import org.yawlfoundation.yawl.editor.ui.swing.TooltipTogglingWidget;
import org.yawlfoundation.yawl.editor.ui.swing.menu.MenuUtilities;

public class CloneSpecificationFromGitAction extends YAWLSpecificationAction implements TooltipTogglingWidget {

	{
	    putValue(Action.SHORT_DESCRIPTION,getDisabledTooltipText());
	    putValue(Action.NAME, "Clone");
	    putValue(Action.LONG_DESCRIPTION, "Clone an existing specification from git");
	    putValue(Action.SMALL_ICON, getMenuIcon("folder_git"));
	    putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_1));
	    putValue(Action.ACCELERATOR_KEY, MenuUtilities.getAcceleratorKeyStroke("1"));
	  }
	public void actionPerformed(ActionEvent event) {
		    
		FileOperations.cloneGit();
		  
	}
	@Override
	public String getDisabledTooltipText() {
		return " You must have no specification open in order to open another from your git ";
	}

	@Override
	public String getEnabledTooltipText() {
		  return " Open an existing specification from your git";
		
	}

}
