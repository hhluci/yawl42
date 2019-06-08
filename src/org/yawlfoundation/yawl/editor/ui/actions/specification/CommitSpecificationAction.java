package org.yawlfoundation.yawl.editor.ui.actions.specification;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.yawlfoundation.yawl.editor.ui.specification.FileOperations;
import org.yawlfoundation.yawl.editor.ui.swing.TooltipTogglingWidget;
import org.yawlfoundation.yawl.editor.ui.swing.menu.MenuUtilities;

public class CommitSpecificationAction extends YAWLSpecificationAction implements TooltipTogglingWidget {

	{
	    putValue(Action.SHORT_DESCRIPTION,getDisabledTooltipText());
	    putValue(Action.NAME, "Commit");
	    putValue(Action.LONG_DESCRIPTION, "Commit the specification file");
	    putValue(Action.SMALL_ICON, getMenuIcon("disk"));
	    putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_2));
	    putValue(Action.ACCELERATOR_KEY, MenuUtilities.getAcceleratorKeyStroke("2"));
	  }
	public void actionPerformed(ActionEvent event) {
		    
		FileOperations.commit();
		  
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
