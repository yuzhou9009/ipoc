package edu.bupt.ipoc.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.asu.emit.qyan.alg.model.Pair;
import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.service.PacketService;
import edu.bupt.ipoc.service.Service;

public class PacketServiceManager{
	
	protected Map<Pair<Integer, Integer>, List<PacketService>> vertex_pair_ps_map = null;
	
	public BasicController bc;
	
	public PacketServiceManager(BasicController _bc)
	{
		vertex_pair_ps_map  = new HashMap<Pair<Integer,Integer>, List<PacketService>>();
		bc = _bc;
	}

	public boolean addService(PacketService ps) {
		
		//PacketService ps = (PacketService)ss;
		
		Pair<Integer,Integer> sd = new Pair<Integer,Integer>(ps.sourceNode,ps.destNode);
		
		if(vertex_pair_ps_map.get(sd) == null)
		{
			List<PacketService> psl = new ArrayList<PacketService>();
			psl.add(ps);
			vertex_pair_ps_map.put(sd, psl);
		}
		else
			vertex_pair_ps_map.get(sd).add(ps);
		return false;
	}

	public boolean deleteService(PacketService ss) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean clearAllServices() {
		for(List<PacketService> psl : vertex_pair_ps_map.values())
		{
			for(PacketService ps : psl)
			{
				ps.cleanMyselfButKeepBWStatistics();
			}
		}
		vertex_pair_ps_map.clear();
		return false;
	}
}
