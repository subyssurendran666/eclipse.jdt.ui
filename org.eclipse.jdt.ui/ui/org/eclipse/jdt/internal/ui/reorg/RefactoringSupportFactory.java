/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
package org.eclipse.jdt.internal.ui.reorg;import org.eclipse.swt.widgets.Shell;import org.eclipse.jface.util.Assert;import org.eclipse.core.runtime.IProgressMonitor;import org.eclipse.core.runtime.NullProgressMonitor;import org.eclipse.jdt.core.ICompilationUnit;import org.eclipse.jdt.core.IPackageFragment;import org.eclipse.jdt.core.JavaModelException;import org.eclipse.jdt.internal.core.refactoring.base.Refactoring;import org.eclipse.jdt.internal.core.refactoring.base.RefactoringStatus;import org.eclipse.jdt.internal.core.refactoring.cus.RenameCompilationUnitRefactoring;import org.eclipse.jdt.internal.core.refactoring.packages.RenamePackageRefactoring;import org.eclipse.jdt.internal.core.refactoring.tagging.IPreactivatedRefactoring;import org.eclipse.jdt.internal.core.refactoring.text.ITextBufferChangeCreator;import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;import org.eclipse.jdt.internal.ui.IPreferencesConstants;import org.eclipse.jdt.internal.ui.JavaPlugin;import org.eclipse.jdt.internal.ui.JavaPluginImages;import org.eclipse.jdt.internal.ui.refactoring.RefactoringWizard;import org.eclipse.jdt.internal.ui.refactoring.RefactoringWizardDialog;import org.eclipse.jdt.internal.ui.refactoring.RenameRefactoringWizard;import org.eclipse.jdt.internal.ui.refactoring.changes.DocumentTextBufferChangeCreator;

public class RefactoringSupportFactory {

	private abstract static class RenameSupport implements IRefactoringRenameSupport {
		private Refactoring fRefactoring;
		private static IProgressMonitor fgNullProgressMonitor= new NullProgressMonitor();

		public boolean canRename(Object element) {
			fRefactoring= createRefactoring(element, new DocumentTextBufferChangeCreator(JavaPlugin.getDefault().getCompilationUnitDocumentProvider()));
			try {
				//FIX ME: must have a better solution to this
				if (fRefactoring instanceof IPreactivatedRefactoring){
					if (((IPreactivatedRefactoring)fRefactoring).checkPreactivation().isOK())
						return true;
				} else { 
				 	if (fRefactoring.checkActivation(fgNullProgressMonitor).isOK())
						return true;
				}	
				fRefactoring= null;
				return false;	
			} catch (JavaModelException e) {
				fRefactoring= null;
				return false;
			}	
		}
		
		public void rename(Object element) {
			Assert.isNotNull(fRefactoring);
			Shell parent= JavaPlugin.getActiveWorkbenchShell();
			RefactoringWizard wizard= createWizard();
			wizard.init(fRefactoring);
			RefactoringWizardDialog dialog= new RefactoringWizardDialog(parent, wizard);
			dialog.open();
			fRefactoring= null;
		}
		
		protected abstract Refactoring createRefactoring(Object element, ITextBufferChangeCreator creator);
		
		protected abstract RefactoringWizard createWizard();
	}
	
	private static class RenamePackage extends RenameSupport {
		protected Refactoring createRefactoring(Object element, ITextBufferChangeCreator creator) {
			return new RenamePackageRefactoring(creator, (IPackageFragment)element);
		}
		
		protected RefactoringWizard createWizard() {
			String title= "Rename Package";
			String message= "Enter the new name for this package. References to all types declared in it will be updated.";
			RenameRefactoringWizard w= new RenameRefactoringWizard(title, message, IJavaHelpContextIds.RENAME_PACKAGE_WIZARD_PAGE, IJavaHelpContextIds.RENAME_PACKAGE_ERROR_WIZARD_PAGE); 
			w.setInputPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_REFACTOR_PACKAGE);
			return w;
		}
	}

	private static class RenameCUnit extends RenameSupport {
		protected Refactoring createRefactoring(Object element, ITextBufferChangeCreator creator) {
			return new RenameCompilationUnitRefactoring(creator, (ICompilationUnit)element);
		}
		
		protected RefactoringWizard createWizard() {
			String title= "Rename Compilation Unit";
			String message= "Enter the new name for this compilation unit. Refactoring will also rename and update references to the type (if any exists) that has the same name as this compilation unit.";
			RenameRefactoringWizard w= new RenameRefactoringWizard(title, message, IJavaHelpContextIds.RENAME_CU_WIZARD_PAGE, IJavaHelpContextIds.RENAME_CU_ERROR_WIZARD_PAGE); 
			w.setInputPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_REFACTOR_CU);
			return w;
		}
	}

	public static IRefactoringRenameSupport createRenameSupport(Object element) {
			
		if (element instanceof IPackageFragment)
			return new RenamePackage();
		
		if (element instanceof ICompilationUnit)
			return new RenameCUnit();
				
		return null;	
	}
}
