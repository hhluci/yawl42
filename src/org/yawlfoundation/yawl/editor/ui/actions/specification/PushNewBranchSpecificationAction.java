package org.yawlfoundation.yawl.editor.ui.actions.specification;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.yawlfoundation.yawl.editor.ui.specification.FileOperations;
import org.yawlfoundation.yawl.editor.ui.swing.TooltipTogglingWidget;
import org.yawlfoundation.yawl.editor.ui.swing.menu.MenuUtilities;

public class PushNewBranchSpecificationAction extends YAWLSpecificationAction implements TooltipTogglingWidget {

	{
	    putValue(Action.SHORT_DESCRIPTION,getDisabledTooltipText());
	    putValue(Action.NAME, "Push New Branch");
	    putValue(Action.LONG_DESCRIPTION, "Push to a new branch");
	    putValue(Action.SMALL_ICON, getMenuIcon("upload-server-icon2"));
	    putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_6));
	    putValue(Action.ACCELERATOR_KEY, MenuUtilities.getAcceleratorKeyStroke("6"));
	  }
	public void actionPerformed(ActionEvent event) {
		    
		FileOperations.pushNewBranch();;
		  
	}
	@Override
	public String getDisabledTooltipText() {
		return "";
	}

	@Override
	public String getEnabledTooltipText() {
		  return "";
		
	}


}
