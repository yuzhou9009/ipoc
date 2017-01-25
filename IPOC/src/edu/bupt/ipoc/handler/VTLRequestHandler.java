package edu.bupt.ipoc.handler;

import java.util.List;
import java.util.Map;

import edu.bupt.ipoc.constraint.Constraint;
import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.service.Service;
import edu.bupt.ipoc.service.VirtualTransLink;

public class VTLRequestHandler{
	
	BasicController bc;
	
	public VTLRequestHandler(BasicController _bc)
	{
		bc = _bc;
	}

	public synchronized boolean handlerRequest(VirtualTransLink vtl, int command, Map<Integer,Constraint> cons) 
	{
		if(command == VirtualTransLink.BUILD_REQUEST)
		{			
			if(vtl.vtl_priority == VirtualTransLink.PRIORITY_HIGH || vtl.vtl_priority == VirtualTransLink.PRIORITY_MID)
			{
				Constraint initBW_c = null;
				
				if(cons != null && cons.size() > 0 && (cons.get(Constraint.INITBW_C) != null))
				{
					initBW_c = cons.get(Constraint.INITBW_C);
				}
				else
				{
					System.out.println("Must have Constraint.INITBW_C");
					return false;
				}
				
				List<Service> _tems = bc.establishNewOnesToFitRequest(vtl, VirtualTransLink.BUILD_REQUEST, cons);
				
				if(_tems !=null && _tems.size() > 0)
				{
					for(Service _ss : _tems)
					{
						bc.mappingServices(vtl, _ss, null);
					}
					return true;
				}				
			}
			else if(vtl.vtl_priority == VirtualTransLink.PRIORITY_LOW)
			{
				Constraint tem = cons.get(Constraint.VTL_PREFER_RESOURCE_C);
				if(tem == null)
				{
					Service _tem = bc.establishNewOneToFitRequest(vtl, Service.UseOpticalService, cons);
					if(_tem != null)
					{
						bc.mappingServices(vtl, _tem, null);
						return true;
					}
					else
					{
						_tem = bc.establishNewOneToFitRequest(vtl, Service.UseOTNService, cons);
						if(_tem != null)
						{
							bc.mappingServices(vtl, _tem, null);
							//TODO
							return true;
						}
					}
				}
				else
				{
					Service _tem = bc.establishNewOneToFitRequest(vtl, tem.value, cons);
					if(_tem != null)
					{
						bc.mappingServices(vtl, _tem, null);
						return true;
					}
				}
				
				
			}
		}
		else if(command == VirtualTransLink.EXTEND_REQUEST)
		{
			if(vtl.vtl_priority == VirtualTransLink.PRIORITY_LOW && cons == null)
			{
				//extend a 10G tunnel.
				Service _tem = bc.establishNewOneToFitRequest(vtl, Service.UseOpticalService, cons);
				if(_tem != null)
				{
					bc.mappingServices(vtl, _tem, null);
					return true;
				}
				else
					return false;
			}
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
		else if(command == VirtualTransLink.SHRINKED_REQUEST)
		{
			//TODO
			List<Service> _tems = vtl.servicesNeededToRemove();
			bc.unmappingServices(vtl,_tems,null);
			return true;
		}
		else if(command == VirtualTransLink.ADJUST_PSS_REQUEST)
		{
			Constraint initBW_c = cons.get(Constraint.INITBW_C);;
			int tem_bw = initBW_c.value;
			
			bc.adjustSpecificVTL(vtl, tem_bw);			
		}

		return false;
	}
}
