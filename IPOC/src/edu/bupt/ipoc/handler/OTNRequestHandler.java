package edu.bupt.ipoc.handler;

import java.util.List;
import java.util.Map;

import edu.bupt.ipoc.constraint.Constraint;
import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.service.OTNService;
import edu.bupt.ipoc.service.Service;
import edu.bupt.ipoc.service.VirtualTransLink;

public class OTNRequestHandler 
		implements RequestHanderInterface{

	BasicController bc;
	
	public OTNRequestHandler(BasicController _bc)
	{
		bc = _bc;
	}
	
	@Override
	public synchronized boolean handlerRequest(Service ss, int command, Map<Integer,Constraint> cons) 
	{
		OTNService otns = null;
		
		if(ss instanceof OTNService)
		{
			otns = (OTNService)ss;
		}
		else
		{
			System.out.println("OTNRequestHandler can only handle OTNService request!");
			return false;
		}
		
		if(command == OTNService.BUILD_REQUEST)
		{
			/*
			 * 			
			_tem = bc.findExistOneToFitRequest(ps,command, cons);
			if(_tem != null)
			{
				//System.out.println("We are here!! "+ _tem.toString());
				bc.mappingServices(ps, _tem, null);
				return true;
			}
			else
			{
				_tem = bc.establishNewOneToFitRequest(ps, command, cons);
				if(_tem != null)
				{
					bc.mappingServices(ps, _tem, null);
					return true;
				}
				else
					return false;
			}*/
			Service _tem = null;
			_tem = bc.findExistOneToFitRequest(otns,command, cons);
			if(_tem != null)
			{
				//System.out.println("We are here!! "+ _tem.toString());
				bc.mappingServices(otns, _tem, null);
				return true;
			}
			
			_tem = bc.establishNewOneToFitRequest(otns, 0, null);
			
			if(_tem != null)
			{
				bc.mappingServices(otns, _tem, null);
				return true;
			}
		}

		return false;

	}


}
