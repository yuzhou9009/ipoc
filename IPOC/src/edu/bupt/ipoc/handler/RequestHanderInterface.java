package edu.bupt.ipoc.handler;

import java.util.List;
import java.util.Map;

import edu.bupt.ipoc.constraint.Constraint;
import edu.bupt.ipoc.service.Service;

public interface RequestHanderInterface {
	
	
	boolean handlerRequest(Service ss, int command, Map<Integer,Constraint> constraints);

}
