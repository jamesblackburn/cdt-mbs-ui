/*******************************************************************************
 * Copyright (c) 2007 Intel Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Intel Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.managedbuilder.ui.wizards;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

/**
 * Interface to be used by extension point:
 * org.eclipse.cdt.managedbuilder.ui.CDTWizard
 * 
 * Implementors should provide 1 or more
 * items in "Project types" list (left pane on
 * the 1st page in any CDT new project wizard) 
 */
public interface ICNewWizard {
	/**
	 * Creates tree items to be displayed in left pane.
	 * 
	 * Method should add 1 or more tree items, 
	 * each of them should have data object attached,
	 * data should be lt;ICProjectTypeHandler&gt; 
     *
	 * @param tree - parent tree object
	 * @param supportedOnly - whether display supported types only
	 */
	void createItems(Tree tree, boolean supportedOnly);
	
	/**
	 * Implementor will be informed about widget where additional
	 * data should be displayed. Normally, it is right pane in the
	 * 1st Wizard page.
	 * 
	 * @param parent - composite where widgets are to be created
	 * @param page   - reference to object which will be informed
	 *                 about changes (usually 1st page in Wizard)
	 *                 May be null if notification is not required
	 *                 or implementor does not really support it. 
	 */
	void setDependentControl(Composite parent, IToolChainListListener page);
}