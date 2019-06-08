package org.yawlfoundation.yawl.editor.ui.actions.specification;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.yawlfoundation.yawl.editor.ui.specification.FileOperations;
import org.yawlfoundation.yawl.editor.ui.swing.TooltipTogglingWidget;
import org.yawlfoundation.yawl.editor.ui.swing.menu.MenuUtilities;

public class PullSpecificationAction extends YAWLSpecificationAction implements TooltipTogglingWidget {

	{
	    putValue(Action.SHORT_DESCRIPTION,getDisabledTooltipText());
	    putValue(Action.NAME, "Pull");
	    putValue(Action.LONG_DESCRIPTION, "Pull from the remote branch");
	    putValue(Action.SMALL_ICON, getMenuIcon("download-server-icon"));
	    putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_4));
	    putValue(Action.ACCELERATOR_KEY, MenuUtilities.getAcceleratorKeyStroke("4"));
	  }
	public void actionPerformed(ActionEvent event) {
		    
		FileOperations.pull();;
		  
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
