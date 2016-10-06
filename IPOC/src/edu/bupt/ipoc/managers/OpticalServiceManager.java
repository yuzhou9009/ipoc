package edu.bupt.ipoc.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.asu.emit.qyan.alg.model.Pair;
import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.service.OpticalService;
import edu.bupt.ipoc.service.Service;
import edu.bupt.ipoc.service.VirtualTransLink;

public class OpticalServiceManager implements ServiceManager{
	
	protected Map<Pair<Integer, Integer>, List<OpticalService>> vertex_pair_os_map = null;
	
	public BasicController bc = null;
	
	public OpticalServiceManager(BasicController _bc)
	{
		bc = _bc;
		vertex_pair_os_map = 
				new HashMap<Pair<Integer,Integer>, List<OpticalService>>();
	}

	@Override
	public boolean addService(Service ss) {
		OpticalService os = (OpticalService)ss;
		Pair<Integer,Integer> sd = new Pair<Integer,Integer>(os.sourceVertex,os.sinkVertex);
		
		if(vertex_pair_os_map.get(sd) == null)
		{
			List<OpticalService> osl = new ArrayList<OpticalService>();
			osl.add(os);
			vertex_pair_os_map.put(sd, osl);
		}
		else
			vertex_pair_os_map.get(sd).add(os);
		
		return false;
	}

	@Override
	public boolean deleteService(Service ss) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public OpticalService findFitOs(int _source, int _dest,int type, int capacityRequest)
	{
		if(type == OpticalService.CHANNEL_10G_FOT_OTN)
		{
			List<OpticalService> _osl = vertex_pair_os_map.get(new Pair<Integer,Integer>(_source,_dest));
			if(_osl!=null && _osl.size()>0)
			{
				for(OpticalService _os : _osl)
				{
					if(_os.type != OpticalService.CHANNEL_10G_FOT_OTN)
						continue;
					if(_os.canOfferEnoughCapacity(capacityRequest))
						return _os;
				}
			}	
		}
		return null;
	}

	@Override
	public boolean clearAllServices() {
		vertex_pair_os_map.clear();
		return false;
	}
	
	public List<OpticalService> getAllOpticalService()
	{
		List<OpticalService> oslall = new ArrayList<OpticalService>();
		for(List<OpticalService> osl : vertex_pair_os_map.values())
		{
			oslall.addAll(osl);
		}
		return oslall;
	}

}
