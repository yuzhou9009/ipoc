package edu.bupt.ipoc.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.asu.emit.qyan.alg.model.Pair;
import edu.asu.emit.qyan.alg.model.Path;
import edu.bupt.ipoc.controller.BasicController;
import edu.bupt.ipoc.service.OpticalService;
import edu.bupt.ipoc.service.Service;
import edu.bupt.ipoc.service.VirtualTransLink;

public class OpticalServiceManager{
	
	protected Map<Pair<Integer, Integer>, List<OpticalService>> vertex_pair_os_map = null;
	
	public BasicController bc = null;
	
	public OpticalServiceManager(BasicController _bc)
	{
		bc = _bc;
		vertex_pair_os_map = 
				new HashMap<Pair<Integer,Integer>, List<OpticalService>>();
	}

	public boolean addService(OpticalService os) 
	{
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

	public boolean deleteService(OpticalService os) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public OpticalService findFitOs(int _source, int _dest,int _type, int capacityRequest)
	{
		if(_type == OpticalService.CHANNEL_10G_FOT_OTN)
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
	
	public List<OpticalService> findFitOss(int _source, int _dest, int _type, int capacityRequest)
	{
		List<OpticalService> _osl = null;
		OpticalService _os = findFitOs(_source,_dest,_type,capacityRequest);
		
		if(_os != null)
		{
			_osl =  new ArrayList<OpticalService>();
			_osl.add(_os);
		}
		else
		{
			if(_type == OpticalService.CHANNEL_10G_FOT_OTN)
			{
				/*
				List<OpticalService> osl_tem = vertex_pair_os_map.get(new Pair<Integer,Integer>(_source,_dest));
				if(osl_tem == null || osl_tem.size() == 0)
					return null;
				System.out.println(osl_tem.size()+"");
				for(OpticalService _ooo : osl_tem)
				{
					System.out.println(_ooo.path+"");
					System.out.println(osl_tem.get(0).path+"");
					
					if(_ooo.path.equals(osl_tem.get(0).path))
						System.out.println("True");
				}
				*/
				//TODO get a list of oss, they offer the otns together. A little complete
				//According to the test this is not that necessary.
			}
			else if(_type == OpticalService.CHANNEL_100G_SINGLE)
			{
				
			}
		}
		return _osl;
	}
	
	public List<OpticalService> findFitOss(int _source, int _dest, int _type, Path _path)
	{
		if(_type != OpticalService.CHANNEL_10G_FOT_OTN)
		{
			return null;
		}
		
		List<OpticalService> osl_tem = vertex_pair_os_map.get(new Pair<Integer,Integer>(_source,_dest));
		List<OpticalService> osl_return = null;
		
		if(osl_tem == null || osl_tem.size() == 0)
			return null;
		osl_return = new ArrayList<OpticalService>();

		for(OpticalService _ooo : osl_tem)
		{			
			if(_ooo.path.equals(_path) && _ooo.type == OpticalService.CHANNEL_10G_FOT_OTN)
				osl_return.add(_ooo);
		}
		
		return osl_return;
	}

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

	public int getAvailableOTNsNumber(int _source, int _dest) {
		// TODO Auto-generated method stub		
		List<OpticalService> _osl = vertex_pair_os_map.get(new Pair<Integer,Integer>(_source,_dest));
		
		if(_osl != null && _osl.size() > 0)
		{
			int count = 0;
			for(OpticalService _oo : _osl)
			{
				if(_oo.type == OpticalService.CHANNEL_10G_FOT_OTN)
				{
					count += (OpticalService.SUB_OTN_NUM - _oo.otn_children.size());
				}
			}
			return count;
		}
		
		return 0;
	}

}
