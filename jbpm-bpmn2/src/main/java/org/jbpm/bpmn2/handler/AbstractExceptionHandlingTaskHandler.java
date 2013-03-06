package org.jbpm.bpmn2.handler;

import org.kie.runtime.process.*;

public abstract class AbstractExceptionHandlingTaskHandler implements WorkItemHandler {

    private WorkItemHandler originalTaskHandler;

    
    public AbstractExceptionHandlingTaskHandler(WorkItemHandler originalTaskHandler) { 
        this.originalTaskHandler = originalTaskHandler;
        unsetLoggingInHandler(originalTaskHandler);
    }
    
    public AbstractExceptionHandlingTaskHandler(Class<? extends WorkItemHandler> originalTaskHandlerClass) { 
        Class [] clsParams = {};
        Object [] objParams = {};
        try { 
            this.originalTaskHandler = originalTaskHandlerClass.getConstructor(clsParams).newInstance(objParams);
        } catch( Exception e ) { 
            throw new UnsupportedOperationException("The " + WorkItemHandler.class.getSimpleName() + " parameter must have a public no-argument constructor." );
        }
        unsetLoggingInHandler(originalTaskHandler);
    }
    
    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        try {
            originalTaskHandler.executeWorkItem(workItem, manager);
        } catch( Throwable cause ) { 
           handleExecuteException(cause, workItem, manager);
        }
    }

    private void unsetLoggingInHandler(WorkItemHandler originalTaskHandler) { 
        if( originalTaskHandler instanceof ServiceTaskHandler ) { 
            ((ServiceTaskHandler) originalTaskHandler).setLogThrownException(false);
        }
    }
    
    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        try {
            originalTaskHandler.abortWorkItem(workItem, manager);
        } catch( RuntimeException re ) { 
           handleAbortException(re, workItem, manager);
        }
    }

    public WorkItemHandler getOriginalTaskHandler() {
        return originalTaskHandler;
    }



    public abstract void handleExecuteException(Throwable cause, WorkItem workItem, WorkItemManager manager);
    public abstract void handleAbortException(Throwable cause, WorkItem workItem, WorkItemManager manager);

    
}
