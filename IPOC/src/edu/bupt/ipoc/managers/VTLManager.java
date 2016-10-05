package edu.bupt.ipoc.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.asu.emit.qyan.alg.model.Pair;
import edu.bupt.ipoc.constraint.Constraint;
import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.service.Service;
import edu.bupt.ipoc.service.VirtualTransLink;

public class VTLManager implements ServiceManager{
	
	protected Map<Pair<Integer, Integer>, List<VirtualTransLink>> vertex_pair_vtl_map = null;
	
	public BasicController bc = null;
	
	public VTLManager(BasicController _bc)
	{
		bc = _bc;
		vertex_pair_vtl_map = 
				new HashMap<Pair<Integer,Integer>, List<VirtualTransLink>>();
	}
	
	public VirtualTransLink findFitVTL(Map<Integer,Constraint> cons)
	{
		if(cons.isEmpty())
			return null;
		
		int source = -1;
		int dest = -1;
		int priority = -1;
		
		Constraint _con_tem;
		_con_tem = cons.get(Constraint.SOURCE_C);
		if(_con_tem != null)
			source = _con_tem.value;
		
		_con_tem = cons.get(Constraint.DEST_C);
		if(_con_tem != null)
			dest = _con_tem.value;
		
		_con_tem = cons.get(Constraint.PRIORITY_C);
		if(_con_tem != null)
			priority = _con_tem.value;
		
		if(source >=0  && dest >= 0 && priority >= 1)
		{
			List<VirtualTransLink> vtll = vertex_pair_vtl_map.get(new Pair<Integer, Integer>(source,dest));
			if(vtll != null && !vtll.isEmpty())
			{
				
				for(VirtualTransLink vtl : vtll)
				{
					_con_tem = cons.get(Constraint.VTL_CARRY_C);
					if(_con_tem !=null && _con_tem.value == VirtualTransLink.CAN_NOT_BE_EXTEND_BUT_SHARE)
					{
						_con_tem = cons.get(Constraint.INITBW_C);
						if(vtl.canOfferMoreBW(_con_tem.value))
							return vtl;
					}
					else
					{
						if(vtl.vtl_priority != priority)
							continue;
						if(!vtl.canCarryMorePS())
							continue;
						_con_tem = cons.get(Constraint.INITBW_C);
						if(_con_tem == null)
							return vtl;
						else
						{
							if(vtl.canOfferMoreBW(_con_tem.value))
								return vtl;
							Map<Integer,Constraint> _cons = new HashMap<Integer,Constraint>();
							
							Constraint _con = new Constraint(Constraint.INITBW_C,_con_tem.value-vtl.getRestBW(),"Cons bw is"+(_con_tem.value-vtl.getRestBW()));

							_cons.put(Constraint.INITBW_C, _con);
							if(bc.handleServiceRequest(vtl, VirtualTransLink.EXTEND_REQUEST,_cons))
								return vtl;
							continue;
						}
					}					
					//System.out.println("vtl source:"+vtl.sourceVertex+"\tdest"+vtl.destVertex+"\tprioiry:"+vtl.vtl_priority);					
				}
			}
			else
				return null;
		}
		else
			System.out.println("No enough constrains");
		
		
		return null;
	}
	
	public List<VirtualTransLink> getAllVTLs()
	{
		List<VirtualTransLink> vtls = new ArrayList<VirtualTransLink>();
		
		for(List<VirtualTransLink> _vtls : vertex_pair_vtl_map.values())
			vtls.addAll(_vtls);
		
		return vtls;
	}

	@Override
	public boolean addService(Service ss) {
		VirtualTransLink vtl = (VirtualTransLink)ss;
		Pair<Integer,Integer> sd = new Pair<Integer,Integer>(vtl.sourceVertex,vtl.destVertex);
		
		if(vertex_pair_vtl_map.get(sd) == null)
		{
			List<VirtualTransLink> vtll = new ArrayList<VirtualTransLink>();
			vtll.add(vtl);
			vertex_pair_vtl_map.put(sd, vtll);
		}
		else
			vertex_pair_vtl_map.get(sd).add(vtl);
		
		return false;
	}

	@Override
	public boolean deleteService(Service ss) {
		// TODO Auto-generated method stub
		return false;
	}
}
