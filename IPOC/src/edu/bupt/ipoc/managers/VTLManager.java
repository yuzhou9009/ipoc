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
	
	//For BE service
	public VirtualTransLink findFitVTL(Map<Integer,Constraint> cons)
	{		
		if(ensureHaveAllNeededConstraints(cons))
		{
			List<VirtualTransLink> vtll = vertex_pair_vtl_map.get(new Pair<Integer, Integer>(cons.get(Constraint.SOURCE_C).value,cons.get(Constraint.DEST_C).value));
			if(vtll != null && !vtll.isEmpty())
			{
				Constraint vtl_carry_type_c = cons.get(Constraint.VTL_CARRY_TYPE_C);
				Constraint init_bw_c = cons.get(Constraint.INITBW_C);
				if(vtl_carry_type_c == null)
				{
					System.out.println("Big mistake, missing vtl_carry_type");
					return null;
				}
				
				//Later todo
				//current for not VTL-BoD, use first fit.
				if(vtl_carry_type_c.value != VirtualTransLink.VTL_BOD)
				{
					for(VirtualTransLink vtl : vtll)
					{
						if(vtl_carry_type_c.value == VirtualTransLink.STATIC_AND_NOT_SHARED)
							continue;
						else if(vtl_carry_type_c.value == VirtualTransLink.STATIC_BUT_SHARED)
						{
							if(vtl.canOfferMoreBW(init_bw_c.value))
								return vtl;
						}
						else if(vtl_carry_type_c.value == VirtualTransLink.DYNAMIC_AND_SHARED_BUT_CONFILICTING)
						{
							if(vtl.vtl_priority != cons.get(Constraint.PRIORITY_C).value)
								continue;
							if(vtl.canOfferMoreBW(init_bw_c.value))
								return vtl;
						}
					}
				}
				else
				{
					List<VirtualTransLink> preferred_vtls = new ArrayList<VirtualTransLink>();
					List<VirtualTransLink> sub_optimal_vtls = new ArrayList<VirtualTransLink>();
					List<VirtualTransLink> last_option_vtls = new ArrayList<VirtualTransLink>();
					for(VirtualTransLink vtl : vtll)
					{
						if(vtl.vtl_priority != cons.get(Constraint.PRIORITY_C).value || !vtl.canCarryMorePS())
							continue;
						if(vtl.canOfferMoreBW(init_bw_c.value))
							preferred_vtls.add(vtl);
						else if(vtl.canOfferMoreBwWithAdjustment(init_bw_c.value))
							sub_optimal_vtls.add(vtl);
						else
							last_option_vtls.add(vtl);
					}
					
					VirtualTransLink t_vtl  = null;
					
					if(preferred_vtls.size()>0)
					{//No need to check null.
						t_vtl = findShortestWithRichestBwVTL(preferred_vtls);
					}
					else if(sub_optimal_vtls.size()>0)
					{//No need to check null.
						t_vtl = findShortestWithRichestBwVTL(preferred_vtls);
						if(!adjustBwAllocationOfBTServices(t_vtl,init_bw_c.value))
							return null;
					}
					else if(last_option_vtls.size()>0)
					{
						//later todo, sort the list.
						for(VirtualTransLink _vtl : last_option_vtls)
						{
							Map<Integer,Constraint> _cons = new HashMap<Integer,Constraint>();
							
							Constraint _con = new Constraint(Constraint.INITBW_C,init_bw_c.value-_vtl.getRestBW(),"Cons bw is"+(init_bw_c.value-_vtl.getRestBW()));

							_cons.put(Constraint.INITBW_C, _con);
							if(bc.handleServiceRequest(_vtl, VirtualTransLink.EXTEND_REQUEST,_cons))
							{
								if(!adjustBwAllocationOfBTServices(t_vtl,init_bw_c.value))
									return null;
								t_vtl = _vtl;
								break;
							}
						}
					}
					return t_vtl;
					
				}
			}
			else
				return null;
		}		
		return null;
	}
	
	public boolean adjustBwAllocationOfBTServices(VirtualTransLink _vtl, int value) {
		// TODO Auto-generated method stub
		//Vary Important
		return false;
	}

	//For BT service
	public List<VirtualTransLink> findFitVTLs(Map<Integer, Constraint> cons) {
		
		if(ensureHaveAllNeededConstraints(cons) && cons.get(Constraint.PRIORITY_C).value == VirtualTransLink.PRIORITY_LOW)//sure
		{
			if(cons.get(Constraint.VTL_CARRY_TYPE_C).value != VirtualTransLink.VTL_BOD)
				return null;
			
			List<VirtualTransLink> vtll = vertex_pair_vtl_map.get(new Pair<Integer, Integer>(cons.get(Constraint.SOURCE_C).value,cons.get(Constraint.DEST_C).value));
			if(vtll != null && !vtll.isEmpty())
			{	
				List<VirtualTransLink> tem_vtll = new ArrayList<VirtualTransLink>();
				int allRequestBW = cons.get(Constraint.INITBW_C).value;
				int canOfferBW = 0;
				for(VirtualTransLink vtl : vtll)
				{
					if((vtl.vtl_priority == VirtualTransLink.PRIORITY_HIGH || vtl.vtl_priority == VirtualTransLink.PRIORITY_MID) && vtl.type == VirtualTransLink.VTL_BOD)
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
					for(VirtualTransLink vtl : vtll)
					{
						if(vtl.vtl_priority == VirtualTransLink.PRIORITY_LOW)
						{
							canOfferBW = vtl.getAcutallyRestBWforShare();
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
		//TODO Auto-generated method stub
		System.out.println("Not done yet!");
		return false;
	}

	public boolean clearAllServices() {		
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

	//Only for BE services
	public VirtualTransLink findShortestWithRichestBwVTL(List<VirtualTransLink> _vtls)
	{
		VirtualTransLink target_vtl = _vtls.get(0);
		
		for(VirtualTransLink _vtl : _vtls)
		{
			if(((int)_vtl.getPathLong()) < ((int)target_vtl.getPathLong()))
				target_vtl = _vtl;
			else if(((int)_vtl.getPathLong()) == ((int)target_vtl.getPathLong()))
			{
				if(_vtl.getRestBW() > target_vtl.getRestBW())
					target_vtl = _vtl;
			}
		}
		return target_vtl;
	}
	
	public void checkVTLStatue() {
		
		int currentStatue = -1;

		List<VirtualTransLink> shrinked_vtl_list = new ArrayList<VirtualTransLink>();
		List<VirtualTransLink> extended_vtl_list = new ArrayList<VirtualTransLink>();
		List<VirtualTransLink> adjusted_vtl_list = new ArrayList<VirtualTransLink>();
		
		for(VirtualTransLink vtl : getAllVTLs())
		{
			vtl.updateBwStatistics();
			currentStatue = vtl.getCurrentStatue();
			if(currentStatue == VirtualTransLink.NEED_TO_BE_ADJUSTED)
			{
				adjusted_vtl_list.add(vtl);
				//System.out.println("NEED_TO_BE_ADJUSTED");
			}
			else if(currentStatue == VirtualTransLink.NEED_TO_BE_EXTENDED)
			{
				extended_vtl_list.add(vtl);
				//System.out.println("NEED_TO_BE_EXTENDED");
			}
			else if(currentStatue == VirtualTransLink.NEED_TO_BE_SHRINKED)
			{
				shrinked_vtl_list.add(vtl);
				//System.out.println("NEED_TO_BE_SHRINKED");
			}
			else
			{
				//do nothing.
			}		
		}

		for(VirtualTransLink vtl : shrinked_vtl_list)
		{
			if(bc.handleServiceRequest(vtl, VirtualTransLink.SHRINKED_REQUEST, null))
			{
				currentStatue = vtl.getCurrentStatue();
				if(currentStatue == VirtualTransLink.NEED_TO_BE_ADJUSTED)
					adjusted_vtl_list.add(vtl);
			}
			else
				System.out.println("Shinked failed, which is impossible!!");
		}
		
		for(VirtualTransLink vtl : extended_vtl_list)
		{
			//TODO Add cons of bw
			Map<Integer,Constraint> consmp = new HashMap<Integer,Constraint>();
			int _tem_extend_bw = -1;
			
			_tem_extend_bw = vtl.howManyMoreBwNeeded();
			//System.out.println("vtl prority is "+vtl.vtl_priority+"\t_tem_extend_bw"+_tem_extend_bw);
			consmp.put(Constraint.INITBW_C, new Constraint(Constraint.INITBW_C,_tem_extend_bw,"vtl's request more bw:"+_tem_extend_bw));
			
			if(bc.handleServiceRequest(vtl, VirtualTransLink.EXTEND_REQUEST, consmp))
			{
				currentStatue = vtl.getCurrentStatue();
				if(currentStatue == VirtualTransLink.NEED_TO_BE_ADJUSTED)
					adjusted_vtl_list.add(vtl);
			}
			else
				System.out.println("Extend not success!!!");
		}
		
		for(VirtualTransLink vtl : adjusted_vtl_list)
		{
			int adjust_bw_value = 0;
			//TODO calulate
			adjustBwAllocationOfBTServices(vtl,adjust_bw_value);
		}	
	}

	public VirtualTransLink findAnExtendedableVTL(Map<Integer, Constraint> cons) 
	{
		if(ensureHaveAllNeededConstraints(cons) && cons.get(Constraint.PRIORITY_C).value == VirtualTransLink.PRIORITY_LOW)//sure
		{
			if(cons.get(Constraint.VTL_CARRY_TYPE_C).value != VirtualTransLink.VTL_BOD)
				return null;
			List<VirtualTransLink> vtll = vertex_pair_vtl_map.get(new Pair<Integer, Integer>(cons.get(Constraint.SOURCE_C).value,cons.get(Constraint.DEST_C).value));
			if(vtll != null && !vtll.isEmpty())
			{	
				for(VirtualTransLink vtl : vtll)
				{
					if(vtl.vtl_priority == VirtualTransLink.PRIORITY_LOW && vtl.canCarryMorePS())
							return vtl;
				}
			}
		}
		return null;
	}

	public List<VirtualTransLink> getVTLs(int sourceVertex, int destVertex) {
		List<VirtualTransLink> vtll = vertex_pair_vtl_map.get(new Pair<Integer, Integer>(sourceVertex,destVertex));
		return vtll;
	}
}
