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
		if(command == Service.PS_CARRIED_REQUEST)// || command == PacketService.Modify)
		{
			cons.put(Constraint.SOURCE_C, new Constraint(Constraint.SOURCE_C,ps.sourceNode,"ps's source id:"+ps.sourceNode));
			cons.put(Constraint.DEST_C, new Constraint(Constraint.DEST_C,ps.destNode,"ps's DEST id:"+ps.destNode));
			cons.put(Constraint.PRIORITY_C, new Constraint(Constraint.PRIORITY_C,ps.priority,"ps's priority:"+ps.priority));

			Constraint _con = cons.get(Constraint.PACKET_SERVICE_CARRIED_TYPE_C);
			if(_con == null)// default: being staticlly carried
			{
				_con = new Constraint(Constraint.PACKET_SERVICE_CARRIED_TYPE_C, PacketService.STATIC_CARRIED, "The ps will be carried in a satatic way!");
				cons.put(Constraint.PACKET_SERVICE_CARRIED_TYPE_C, _con);
			}									
			ps.setServiceCarriedType(_con.value);
			
			cons.put(Constraint.INITBW_C, new Constraint(Constraint.INITBW_C,ps.getCurrentOccupiedBw(),"ps's request bw:"+ps.getCurrentOccupiedBw()));
			 
			if(ps.priority == PacketService.PRIORITY_LOW && _con.value == PacketService.DYNAMICALLY_CARRIED_AND_DIVISIBLE)
			{	//BT Service
				List<Service> _tems = null;
				
				_tems = bc.getOnesToFitRequest(ps, command, cons);
				if(_tems != null && _tems.size()>0)
				{
					bc.mappingServices(ps, _tems, cons);
					return true;					
				}
			}
			else//BE service
			{
				Service _tem = null;
				
				_tem = bc.getOneToFitRequest(ps, command, cons);
				if(_tem != null)
				{
					//System.out.println("PS ID:"+ps.id+"\n");
					bc.mappingServices(ps, _tem, null);
					return true;
				}
			}				
		}
		return false;
	}
}
