package edu.bupt.ipoc.handler;

import java.util.List;
import java.util.Map;

import edu.bupt.ipoc.constraint.Constraint;
import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.service.Service;
import edu.bupt.ipoc.service.VirtualTransLink;

public class VTLRequestHandler
		implements RequestHanderInterface{
	
	public static final int UseOpticalService = 11;
	
	BasicController bc;
	
	public VTLRequestHandler(BasicController _bc)
	{
		bc = _bc;
	}

	@Override
	public synchronized boolean handlerRequest(Service ss, int command, Map<Integer,Constraint> cons) 
	{
		VirtualTransLink vtl = null;
		
		if(ss instanceof VirtualTransLink)
		{
			vtl = (VirtualTransLink)ss;
		}
		else
		{
			System.out.println("VTLRequestHandler can only handle VirtualTransLink request!");
			return false;
		}
		
		if(command == VirtualTransLink.BUILD_REQUEST)
		{
			Constraint initBW_c = null;
			
			if(cons != null && cons.size() > 0)
			{
				initBW_c = cons.get(Constraint.INITBW_C);

				if(initBW_c == null)
				{
					System.out.println("Must have Constraint.INITBW_C");
					return false;
				}
			}
			else
			{
				System.out.println("Must have Constraint.INITBW_C");
				return false;
			}

//			int ask_bw = initBW_c.value;
			
			if(vtl.vtl_priority == VirtualTransLink.VTL_P_HIGH || vtl.vtl_priority == VirtualTransLink.VTL_P_MID)
			{
				List<Service> _tems = bc.establishNewOnesToFitRequest(vtl, VirtualTransLink.BUILD_REQUEST, cons);
				
				if(_tems !=null && _tems.size() > 0)
				{
					for(Service _ss : _tems)
					{
						bc.mappingServices(vtl, _ss, null);
					}
					return true;
				}
				//}
				
			}
			else if(vtl.vtl_priority == VirtualTransLink.VTL_P_LOW)
			{
				Service _tem = bc.establishNewOneToFitRequest(vtl, UseOpticalService, null);
				 if(_tem != null)
				{
					bc.mappingServices(vtl, _tem, null);
					return true;
				}
			}
		}
		else if(command == VirtualTransLink.EXTEND_REQUEST)
		{
			List<Service> _tems = bc.establishNewOnesToFitRequest(vtl, VirtualTransLink.EXTEND_REQUEST, cons);
			
			if(_tems !=null && _tems.size() > 0)
			{
				for(Service _ss : _tems)
				{
					bc.mappingServices(vtl, _ss, null);
				}
				return true;
			}
		}

		return false;
	}
	
/*	public int getBWConstraint(List<Constraint> cons)
	{
		int initBW_c = -1;;
		
		if(cons != null && cons.size() > 0)
		{
			for(Constraint _con : cons)
			{
				if(_con.type == Constraint.INITBW_C)
				{
					initBW_c = _con.value;
					break;
				}
				
			}
		}
		
		return initBW_c;
	}
	*/


}
