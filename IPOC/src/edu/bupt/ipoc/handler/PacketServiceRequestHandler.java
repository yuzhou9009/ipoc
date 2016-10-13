package edu.bupt.ipoc.handler;

import java.util.List;
import java.util.Map;

import edu.bupt.ipoc.constraint.Constraint;
import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.service.PacketService;
import edu.bupt.ipoc.service.Service;

public class PacketServiceRequestHandler{

	BasicController bc;
	
	public PacketServiceRequestHandler(BasicController _bc)
	{
		bc = _bc;
	}

	public synchronized boolean handlerRequest(PacketService ps, int command, Map<Integer,Constraint> cons) 
	{
		if(command == PacketService.CARRIED_REQUEST)// || command == PacketService.Modify)
		{
			cons.put(Constraint.SOURCE_C, new Constraint(Constraint.SOURCE_C,ps.sourceVertex,"ps's source id:"+ps.sourceVertex));
			cons.put(Constraint.DEST_C, new Constraint(Constraint.DEST_C,ps.sinkVertex,"ps's DEST id:"+ps.sinkVertex));
			cons.put(Constraint.PRIORITY_C, new Constraint(Constraint.PRIORITY_C,ps.s_priority,"ps's priority:"+ps.s_priority));
			cons.put(Constraint.INITBW_C, new Constraint(Constraint.INITBW_C,ps.getCurrentBw(),"ps's request bw:"+ps.getCurrentBw()));
			
			Constraint _con = cons.get(Constraint.VTL_CARRY_TYPE_C);
			if(_con !=null && _con.value == PacketService.VTL_BOD && ps.s_priority == PacketService.SP_LOW)
			{
				List<Service> _tems = null;
				_tems = bc.findExistOnesToFitRequest(ps,command, cons);
				if(_tems != null && _tems.size()>0)
				{
					bc.mappingServices(ps, _tems, cons);
					return true;					
				}				
			}
			else
			{
				Service _tem = null;
				_tem = bc.findExistOneToFitRequest(ps,command, cons);
				if(_tem != null)
				{
					bc.mappingServices(ps, _tem, null);
					return true;
				}
			}
			
			//if we cannot find appropriate vtl(s)
			Service _tem = null;
			_tem = bc.establishNewOneToFitRequest(ps, command, cons);
			if(_tem != null)
			{
				bc.mappingServices(ps, _tem, null);
				return true;
			}
			else
				return false;
		}
		return false;
	}
}
