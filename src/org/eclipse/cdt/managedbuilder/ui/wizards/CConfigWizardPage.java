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

import java.util.ArrayList;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IProjectType;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.ui.newui.ManagedBuilderUIImages;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class CConfigWizardPage extends MBSCustomPage {

	public static final String PAGE_ID = "org.eclipse.cdt.managedbuilder.ui.wizard.CConfigWizardPage"; //$NON-NLS-1$

	private static final Image IMG = ManagedBuilderUIImages.get(ManagedBuilderUIImages.IMG_BUILD_CONFIG);
	private static final String TITLE = IDEWorkbenchMessages.getString("CConfigWizardPage.0"); //$NON-NLS-1$
	private static final String MESSAGE = IDEWorkbenchMessages.getString("CConfigWizardPage.1"); //$NON-NLS-1$
	private static final String DELIMITER = "_with_";  //$NON-NLS-1$
	private static final String EMPTY_STR = "";  //$NON-NLS-1$
	
	ICWizardHandler handler;
	Table table;
	CheckboxTableViewer tv;
	Label l_projtype;
	Label l_chains;

	Composite parent;
	String errorMessage = null;
	String message = MESSAGE;
	boolean isVisible = false;
	Button propButton;
	
	public CConfigWizardPage() { pageID = PAGE_ID; }
	
	private CfgItem[] getCfgItems(boolean getDefault) {
		if (handler == null) return null; 
		CfgItem[] its;
		if (getDefault)  
			its = cfgItems(getDefaultCfgs(handler));
		else {
			ArrayList out = new ArrayList(table.getItemCount());
			TableItem[] tis = table.getItems();
			for (int i=0; i < tis.length; i++) {
				if (tis[i].getChecked())
					out.add(tis[i].getData()); 
			}
			its = (CfgItem[])out.toArray(new CfgItem[out.size()]);
		}
		return its;
	}
	
	public IConfiguration[] getConfigurations(boolean getDefault) {
		CfgItem[] its = getCfgItems(getDefault);
		if (its == null) return new IConfiguration[0];
		IConfiguration[] cfgs = new IConfiguration[its.length];
		for (int i=0; i<its.length; i++) cfgs[i] = its[i].cfg;
		return cfgs;
	}
	
	public String[] getNames(boolean getDefault) {
		CfgItem[] its = getCfgItems(getDefault);
		if (its == null) return new String[0];
		String [] names = new String[its.length];
		for (int i=0; i<its.length; i++) names[i] = its[i].name;
		return names;
	}
	
	public void createControl(Composite p) {
		parent = new Composite(p, SWT.NONE);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout(3, false));
		
		setupLabel(parent, IDEWorkbenchMessages.getString("CConfigWizardPage.4"), 1, GridData.BEGINNING); //$NON-NLS-1$
		l_projtype = setupLabel(parent, EMPTY_STR, 2, GridData.FILL_HORIZONTAL);
		setupLabel(parent, IDEWorkbenchMessages.getString("CConfigWizardPage.5"), 1, GridData.BEGINNING); //$NON-NLS-1$
		l_chains = setupLabel(parent, EMPTY_STR, 2, GridData.FILL_HORIZONTAL);
		setupLabel(parent, IDEWorkbenchMessages.getString("CConfigWizardPage.6"), 3, GridData.BEGINNING); //$NON-NLS-1$
		
		table = new Table(parent, SWT.BORDER | SWT.CHECK);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		table.setLayoutData(gd);
		
		tv = new CheckboxTableViewer(table);
		tv.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				return (Object[])inputElement;
			}
			public void dispose() {}
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
		});
		tv.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return element == null ? EMPTY_STR : ((CfgItem)element).name;//$NON-NLS-1$
			}
			public Image getImage(Object element) { return IMG; }
		});
		tv.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				isCustomPageComplete();
				update();
			}});
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		c.setLayout(new GridLayout(1, false));

		Button b1 = new Button(c, SWT.PUSH);
		b1.setText(IDEWorkbenchMessages.getString("CConfigWizardPage.7")); //$NON-NLS-1$
		b1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		b1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) { 
				tv.setAllChecked(true);
				isCustomPageComplete();
				update();
			}});

		Button b2 = new Button(c, SWT.PUSH);
		b2.setText(IDEWorkbenchMessages.getString("CConfigWizardPage.8")); //$NON-NLS-1$
		b2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		b2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tv.setAllChecked(false);
				isCustomPageComplete();
				update();
			}});
		propButton = new Button(parent, SWT.CHECK);
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 3;
		propButton.setLayoutData(gd);
		propButton.setText(IDEWorkbenchMessages.getString("CConfigWizardPage.9")); //$NON-NLS-1$
		propButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handler.setShowProperties(((Button)e.widget).getSelection());
			}});
	}

	/**
	 * 
	 * @param handler
	 * @return
	 */
	static public IConfiguration[] getDefaultCfgs(ICWizardHandler handler) {
		IToolChain[] tcs = handler.getSelectedToolChains();
		String id = handler.getPropertyId();
		IProjectType pt = handler.getProjectType(); 
		ArrayList out = new ArrayList(tcs.length * 2);
		for (int i=0; i < tcs.length; i++) {
			IConfiguration[] cfgs = null;
			if (id != null) 
				cfgs = ManagedBuildManager.getExtensionConfigurations(tcs[i], ICWizardHandler.ARTIFACT, id);
			else if (pt != null) 
				cfgs = ManagedBuildManager.getExtensionConfigurations(tcs[i], pt);
			if (cfgs == null) return null;
			
			for (int j=0; j<cfgs.length; j++) {
				if (cfgs[j].isSystemObject() || (handler.supportedOnly() && !cfgs[j].isSupported())) continue;
				out.add(cfgs[j]);
			}
		}
		return (IConfiguration[])out.toArray(new IConfiguration[out.size()]);
	}
	
    /**
     * Returns whether this page's controls currently all contain valid 
     * values.
     *
     * @return <code>true</code> if all controls are valid, and
     *   <code>false</code> if at least one is invalid
     */
    public boolean isCustomPageComplete() {
    	if (!isVisible) return true;
    	
		if (table.getItemCount() == 0) {
			errorMessage = IDEWorkbenchMessages.getString("CConfigWizardPage.10"); //$NON-NLS-1$
			message = errorMessage; 
			return false;
		}
		if (tv.getCheckedElements().length == 0) {
			errorMessage = IDEWorkbenchMessages.getString("CConfigWizardPage.11"); //$NON-NLS-1$
			message = errorMessage; 
			return false;
		}
		errorMessage = null;
		message = MESSAGE;
        return true;
    }

    /**
     * 
     */
    public void setVisible(boolean visible) {
    	isVisible = visible;
		if (handler != null) {
			if (handler.needsConfig()) { 
				tv.setInput(cfgItems(getDefaultCfgs(handler)));
				tv.setAllChecked(true);
//				setPageComplete(validatePage());
				if (visible) {
					String s = EMPTY_STR;
					IToolChain[] tc = handler.getSelectedToolChains();
					for (int i=0; i < tc.length; i++) {
						s = s + tc[i].getName();
						if (i < tc.length - 1) s = s + ", ";  //$NON-NLS-1$
					}
					l_chains.setText(s);
					l_projtype.setText(handler.getName());
				}
			}
		}
		propButton.setSelection(handler.showProperties());
		parent.setVisible(visible);
		if (visible) update();
    }

    class CfgItem {
    	String name;
    	IConfiguration cfg;
    	CfgItem(IConfiguration _cfg) { 
    		cfg = _cfg;
    		name = cfg.getName();
    	}
    }
    
    private boolean hasDoubles(CfgItem[] its) {
   		for (int i=0; i<its.length; i++) {
       		String s = its[i].name;
       		for (int j=0; j<its.length; j++) {
       			if (i == j) continue;
       			if (s.equals(its[j].name)) 
       				return true;
       		}
       	}
   		return false;
    }
    
    private CfgItem[] cfgItems(IConfiguration[] cfgs) {
    	CfgItem[] its = new CfgItem[cfgs.length];
    	for (int i=0; i<cfgs.length; i++)
    		its[i] = new CfgItem(cfgs[i]);
    	
    	// if names are not unique, add toolchain name
    	if (hasDoubles(its)) {
       		for (int k=0; k<its.length; k++) {
       			its[k].name = its[k].name + DELIMITER + its[k].cfg.getToolChain().getName();
       		}
		}
    	// if names are still not unique, add index
    	if (hasDoubles(its)) {
       		for (int k=0; k<its.length; k++) {
       			its[k].name = its[k].name + k;
       		}
		}
    	return its;
    }
    
	//------------------------
    private Label setupLabel(Composite c, String name, int span, int mode) {
		Label l = new Label(c, SWT.NONE);
		l.setText(name);
		GridData gd = new GridData(mode);
		gd.horizontalSpan = span;
		l.setLayoutData(gd);
		Composite p = l.getParent();
		l.setFont(p.getFont());
		return l;
	}

	public void setHandler(ICWizardHandler h) { handler = h; }

	public String getName() { return TITLE; }
	public void dispose() {}
	public Control getControl() { return parent; }
	public String getDescription() { return null; }
	public String getErrorMessage() { return errorMessage; }
	public Image getImage() { return wizard.getDefaultPageImage(); }
	public String getMessage() { return message; }
	public String getTitle()   { return TITLE; }
	public void performHelp() {}
	public void setDescription(String description) {}
	public void setImageDescriptor(ImageDescriptor image) {}
	public void setTitle(String _title) {}

	protected void update() {
		wizard.getContainer().updateButtons();
		wizard.getContainer().updateMessage();
		wizard.getContainer().updateTitleBar();
	}
}