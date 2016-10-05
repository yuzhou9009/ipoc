package edu.bupt.ipoc.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.asu.emit.qyan.alg.model.Pair;
import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.service.PacketService;
import edu.bupt.ipoc.service.Service;

public class PacketServiceManager implements ServiceManager{
	
	protected Map<Pair<Integer, Integer>, List<PacketService>> vertex_pair_ps_map = null;
	
	public BasicController bc;
	
	public PacketServiceManager(BasicController _bc)
	{
		vertex_pair_ps_map  = new HashMap<Pair<Integer,Integer>, List<PacketService>>();
		bc = _bc;
	}

	@Override
	public boolean addService(Service ss) {
		
		PacketService ps = (PacketService)ss;
		
		Pair<Integer,Integer> sd = new Pair<Integer,Integer>(ps.sourceVertex,ps.sinkVertex);
		
		if(vertex_pair_ps_map.get(sd) == null)
		{
			List<PacketService> psl = new ArrayList<PacketService>();
			psl.add(ps);
			vertex_pair_ps_map.put(sd, psl);
		}
		else
			vertex_pair_ps_map.get(sd).add(ps);
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteService(Service ss) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean clearAllServices() {
		vertex_pair_ps_map.clear();
		return false;
	}
}
