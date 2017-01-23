package edu.bupt.ipoc.handler;

import java.util.List;
import java.util.Map;

import edu.bupt.ipoc.constraint.Constraint;
import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.service.OTNService;
import edu.bupt.ipoc.service.Service;

public class OTNRequestHandler {

	BasicController bc;
	
	public OTNRequestHandler(BasicController _bc)
	{
		bc = _bc;
	}
	
	public synchronized boolean handlerRequest(OTNService otns, int command, Map<Integer,Constraint> cons) 
	{		
		if(command == Service.OTN_BUILD_REQUEST)
		{
			Service _tem = null;
			_tem = bc.findExistOneToFitRequest(otns,command, cons);
			if(_tem != null)
			{
				bc.mappingServices(otns, _tem, null);
				return true;
			}
			
			_tem = bc.establishNewOneToFitRequest(otns, Service.OTN_BUILD_REQUEST, null);
			
			if(_tem != null)
			{
				bc.mappingServices(otns, _tem, null);
				return true;
			}
		}

		return false;

	}


}
