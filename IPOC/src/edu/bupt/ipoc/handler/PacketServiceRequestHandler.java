package edu.bupt.ipoc.handler;

import java.util.List;
import java.util.Map;

import edu.bupt.ipoc.constraint.Constraint;
import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.service.PacketService;
import edu.bupt.ipoc.service.Service;

public class PacketServiceRequestHandler 
		implements RequestHanderInterface {

//	OpticalResourceRequestHandler orrh = null;
//	VTLRequestHandler vtlrh = null;
//	OTNRequestHandler otnrh = null;
	//ServiceManager sm = null;
	
	BasicController bc;
	
	public PacketServiceRequestHandler(BasicController _bc)
	{
		bc = _bc;
	}

	@Override
	public synchronized boolean handlerRequest(Service _ss, int command, Map<Integer,Constraint> cons) 
	{
		PacketService ps = null;
		if(_ss instanceof PacketService)
		{
			ps = (PacketService)_ss;
		}
		else
		{
			System.out.println("PacketServiceRequestHandler can only handle PacketService request!");
			return false;
		}
		if(command == PacketService.CARRIED_REQUEST)// || command == PacketService.Modify)
		{
			Service _tem = null;
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
			}
		}

		return false;
	}
}
