package edu.bupt.ipoc.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.asu.emit.qyan.alg.model.Pair;
import edu.bupt.ipoc.constraint.Constraint;
import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.service.Service;
import edu.bupt.ipoc.service.ServiceGenerator;
import edu.bupt.ipoc.service.VirtualTransLink;

public class VTLManager{
	
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
		if(ensureHaveAllNeededConstraints(cons))
		{
			List<VirtualTransLink> vtll = vertex_pair_vtl_map.get(new Pair<Integer, Integer>(cons.get(Constraint.SOURCE_C).value,cons.get(Constraint.DEST_C).value));
			if(vtll != null && !vtll.isEmpty())
			{
				Constraint _con_tem;
				for(VirtualTransLink vtl : vtll)
				{					
					_con_tem = cons.get(Constraint.VTL_CARRY_TYPE_C);
					if(_con_tem !=null && _con_tem.value == VirtualTransLink.CAN_NOT_BE_EXTENDED_BUT_SHARED)
					{
						if(vtl.vtl_priority != cons.get(Constraint.PRIORITY_C).value)
							continue;
						_con_tem = cons.get(Constraint.INITBW_C);
						if(vtl.canOfferMoreBW(_con_tem.value))
							return vtl;
					}
					else if(_con_tem !=null && _con_tem.value == VirtualTransLink.CAN_NOT_BE_EXTENDED_OR_SHARED)
					{
						continue;
					}
					else if(_con_tem !=null && _con_tem.value == VirtualTransLink.VTL_BOD)
					{
						if(vtl.vtl_priority != cons.get(Constraint.PRIORITY_C).value)
							continue;
						if(!vtl.canCarryMorePS())
							continue;
						_con_tem = cons.get(Constraint.INITBW_C);
						
						if(vtl.canOfferMoreBW(_con_tem.value))
							return vtl;
						Map<Integer,Constraint> _cons = new HashMap<Integer,Constraint>();
						
						Constraint _con = new Constraint(Constraint.INITBW_C,_con_tem.value-vtl.getRestBW(),"Cons bw is"+(_con_tem.value-vtl.getRestBW()));

						_cons.put(Constraint.INITBW_C, _con);
						if(bc.handleServiceRequest(vtl, VirtualTransLink.EXTEND_REQUEST,_cons))
							return vtl;
						continue;
					}					
					//System.out.println("vtl source:"+vtl.sourceVertex+"\tdest"+vtl.destVertex+"\tprioiry:"+vtl.vtl_priority);					
				}
				//to be extended, if did not find any for vtl_bod mode, we would check if any vtl 
			}
			else
				return null;
		}		
		return null;
	}
	
	public List<VirtualTransLink> findFitVTLs(Map<Integer, Constraint> cons) {
		
		if(ensureHaveAllNeededConstraints(cons) && cons.get(Constraint.PRIORITY_C).value == VirtualTransLink.PRIORITY_LOW)//sure
		{
			List<VirtualTransLink> vtll = vertex_pair_vtl_map.get(new Pair<Integer, Integer>(cons.get(Constraint.SOURCE_C).value,cons.get(Constraint.DEST_C).value));
			if(vtll != null && !vtll.isEmpty())
			{	
				List<VirtualTransLink> tem_vtll = new ArrayList<VirtualTransLink>();
				int allRequestBW = cons.get(Constraint.INITBW_C).value;
				int canOfferBW = 0;
				for(VirtualTransLink vtl : vtll)
				{
					if(vtl.vtl_priority == VirtualTransLink.PRIORITY_HIGH || vtl.vtl_priority == VirtualTransLink.PRIORITY_MID)
					{
						canOfferBW = vtl.getAcutallyRestBWforShare();//getAcutallyRestBW();
						if(canOfferBW > 0)
						{
							allRequestBW -= canOfferBW;
							tem_vtll.add(vtl);
							if(allRequestBW <= 0)
							{
								return tem_vtll;
							}
						}
						else if(canOfferBW == 0)
							;
						else
							System.out.println("There must be some big mistake!! the canOfferBW is:"+canOfferBW);
					}
				}
				if(allRequestBW > 0)
				{
					for(VirtualTransLink vtl : vtll)//there need to be modified, the low flow should can be carried in different VTL_OS
					{
						if(vtl.vtl_priority == VirtualTransLink.PRIORITY_LOW)
						{
							if(vtl.canOfferMoreBW(allRequestBW))
							{
								allRequestBW = 0;
								tem_vtll.add(vtl);
								return tem_vtll;
							}
						}
					}
				}
			}			
		}
		
		return null;
	}
	
	public List<VirtualTransLink> getAllVTLs()
	{
		List<VirtualTransLink> vtls = new ArrayList<VirtualTransLink>();
		
		for(List<VirtualTransLink> _vtls : vertex_pair_vtl_map.values())
			vtls.addAll(_vtls);
		
		return vtls;
	}
	
	public void showAllVirtualTransLink()
	{
		List<VirtualTransLink> _vtls = getAllVTLs();
		System.out.println("There are "+_vtls.size()+" vtls");
		for(VirtualTransLink _vtl : _vtls)
		{
			_vtl.showMyself();
		}
	}
	
	public Map<Pair<Integer, Integer>, List<VirtualTransLink>> getVTLMAP()
	{
		return this.vertex_pair_vtl_map;
	}

	public boolean addService(VirtualTransLink vtl) 
	{
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

	public boolean deleteService(VirtualTransLink vtl) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean clearAllServices() {
		// TODO Auto-generated method stub
		
		vertex_pair_vtl_map.clear();
		
		return false;
	}
	
	public boolean ensureHaveAllNeededConstraints(Map<Integer,Constraint> cons)
	{
		if(cons.isEmpty())
		{
			System.out.println("All constraints miss, check it");
			return false;
		}
		
		int source = -1;
		int dest = -1;
		int priority = -1;
		int allRequestBW = -1;
		
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
		
		_con_tem = cons.get(Constraint.INITBW_C);
		if(_con_tem != null)
			allRequestBW = _con_tem.value;
		if(source >=0  && dest >= 0 && priority >=1 && allRequestBW >= 1)
			return true;
		System.out.println("Some constraint(s) may miss, check it");
		return false;
	}
}
