package org.yawlfoundation.yawl.editor.ui.actions.specification;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.yawlfoundation.yawl.editor.ui.specification.FileOperations;
import org.yawlfoundation.yawl.editor.ui.swing.TooltipTogglingWidget;
import org.yawlfoundation.yawl.editor.ui.swing.menu.MenuUtilities;

public class SetGitParasAction extends YAWLSpecificationAction implements TooltipTogglingWidget {

	{
	    putValue(Action.SHORT_DESCRIPTION,getDisabledTooltipText());
	    putValue(Action.NAME, "Set");
	    putValue(Action.LONG_DESCRIPTION, "Set git paras");
	    putValue(Action.SMALL_ICON, getMenuIcon("page_white_gear"));
	    putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_0));
	    putValue(Action.ACCELERATOR_KEY, MenuUtilities.getAcceleratorKeyStroke("0"));
	  }
	public void actionPerformed(ActionEvent event) {
		    
		FileOperations.gitParas();
		  
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