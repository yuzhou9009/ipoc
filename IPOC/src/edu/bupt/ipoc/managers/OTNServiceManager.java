package edu.bupt.ipoc.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.asu.emit.qyan.alg.model.Pair;
import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.service.OTNService;

public class OTNServiceManager{
	

	
	protected Map<Pair<Integer, Integer>, List<OTNService>> vertex_pair_otns_map = null;

	public BasicController bc = null;
	
	public OTNServiceManager(BasicController _bc)
	{
		bc = _bc;
		vertex_pair_otns_map = 
				new HashMap<Pair<Integer,Integer>, List<OTNService>>();
	}

	public boolean addService(OTNService otns) 
	{
		Pair<Integer,Integer> sd = new Pair<Integer,Integer>(otns.sourceVertex,otns.destVertex);
		
		if(vertex_pair_otns_map.get(sd) == null)
		{
			List<OTNService> otnsl = new ArrayList<OTNService>();
			otnsl.add(otns);
			vertex_pair_otns_map.put(sd,otnsl);
		}
		else
			vertex_pair_otns_map.get(sd).add(otns);
		
		return false;
	}

	public boolean deleteService(OTNService otns) {
		List<OTNService> otnsl = vertex_pair_otns_map.get(new Pair<Integer,Integer>(otns.sourceVertex,otns.destVertex));
		
		if(otnsl != null && otnsl.size() > 0)
			return otnsl.remove(otns);
		
		return false;
	}

	public boolean clearAllServices() {
		vertex_pair_otns_map.clear();
		return false;
	}
}
