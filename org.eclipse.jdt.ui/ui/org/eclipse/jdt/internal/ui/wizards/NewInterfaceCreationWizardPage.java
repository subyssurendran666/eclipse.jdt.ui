/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
package org.eclipse.jdt.internal.ui.wizards;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.ui.help.WorkbenchHelp;

import org.eclipse.jdt.core.IJavaElement;

import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.dialogs.StatusUtil;

public class NewInterfaceCreationWizardPage extends TypePage {
	
	private final static String PAGE_NAME= "NewInterfaceCreationWizardPage"; //$NON-NLS-1$
		
	public NewInterfaceCreationWizardPage(IWorkspaceRoot root) {
		super(false, PAGE_NAME, root);
		
		setTitle(NewWizardMessages.getString("NewInterfaceCreationWizardPage.title")); //$NON-NLS-1$
		setDescription(NewWizardMessages.getString("NewInterfaceCreationWizardPage.description"));			 //$NON-NLS-1$
	}

	// -------- Initialization ---------

	/**
	 * Should be called from the wizard with the input element.
	 */
	public void init(IStructuredSelection selection) {
		IJavaElement jelem= getInitialJavaElement(selection);
			
		initContainerPage(jelem);
		initTypePage(jelem);
		updateStatus(findMostSevereStatus());
	}		
	
	// ------ validation --------
	
	/**
	 * Finds the most severe error (if there is one)
	 */
	private IStatus findMostSevereStatus() {
		return StatusUtil.getMostSevere(new IStatus[] {
			fContainerStatus,
			isEnclosingTypeSelected() ? fEnclosingTypeStatus : fPackageStatus,
			fTypeNameStatus,
			fModifierStatus,
			fSuperInterfacesStatus
		});		
	}
		
	/**
	 * @see ContainerPage#handleFieldChanged
	 */
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		
		updateStatus(findMostSevereStatus());
	}
	
	
	// ------ ui --------
	
	/**
	 * @see WizardPage#createControl
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
		
		int nColumns= 4;
		
		GridLayout layout= new GridLayout();
		//layout.minimumWidth= convertWidthInCharsToPixels(80);
		layout.numColumns= nColumns;		
		composite.setLayout(layout);
		
		createContainerControls(composite, nColumns);	
		createPackageControls(composite, nColumns);	
		createEnclosingTypeControls(composite, nColumns);
				
		createSeparator(composite, nColumns);
		
		createTypeNameControls(composite, nColumns);
		createModifierControls(composite, nColumns);

		// createSeparator(composite, nColumns);
		
		createSuperInterfacesControls(composite, nColumns);
						
		setControl(composite);
		
		setFocus();
		WorkbenchHelp.setHelp(composite, IJavaHelpContextIds.NEW_INTERFACE_WIZARD_PAGE);		
	}
	
}