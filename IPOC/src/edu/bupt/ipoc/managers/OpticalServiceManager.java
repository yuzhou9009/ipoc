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
		
		return true;
	}

	public boolean deleteService(OpticalService os) {
		
		List<OpticalService> _osl = vertex_pair_os_map.get(new Pair<Integer,Integer>(os.sourceVertex,os.sinkVertex));
		if(_osl != null && _osl.size() > 0)
		{
			if(_osl.remove(os))
				return bc.handleServiceRequest(os, OpticalService.DELETE_RELEASE, null);
			else
				System.out.println("big mistake");
		}
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
	
	public List<OpticalService> findFitOss(int _source, int _dest, int _type, int capacityRequest, boolean f_strict_routing)
	{
		if(_type != OpticalService.CHANNEL_10G_FOT_OTN)
		{
			System.out.println("Please check the input of findFitOss");
			return null;
		}
		int request_otn_number = (capacityRequest-1)/Service.BW_1G + 1;
		
		List<List<OpticalService>> _osl = new ArrayList<List<OpticalService>>();
		
		List<OpticalService> _tem = vertex_pair_os_map.get(new Pair<Integer,Integer>(_source, _dest));
		
		if(_tem !=null && f_strict_routing)
		{
			for(OpticalService _os : _tem)
			{
				if(_os.type == OpticalService.CHANNEL_10G_FOT_OTN && _os.getNumberofFreeOTNs() > 0)
				{
					if(_osl.size() > 0)
					{
						for(List<OpticalService> sub_list : _osl)
						{
							if(sub_list.get(0).path.equals(_os.path))
							{
								sub_list.add(_os);
								if(getAvailableOTNsNumber(sub_list) >= request_otn_number)
									return sub_list;
								break;
							}
						}
						List<OpticalService> new_list = new ArrayList<OpticalService>();
						new_list.add(_os);
						_osl.add(new_list);	
						if(getAvailableOTNsNumber(new_list) >= request_otn_number)
							return new_list;
						//
					}
					else
					{
						List<OpticalService> new_list = new ArrayList<OpticalService>();
						new_list.add(_os);
						_osl.add(new_list);
						if(getAvailableOTNsNumber(new_list) >= request_otn_number)
							return new_list;
						//
					}
				}
			}	
			return null;
		}
		else if(_tem !=null )
		{
			_tem = findFitOss(_source,_dest,_type,null);
			
			if(_tem != null && _tem.size() > 0 && getAvailableOTNsNumber(_tem) >= request_otn_number)
				return _tem;
			return null;
		}
		return null;
	}
	
	public int getAvailableOTNsNumber(List<OpticalService> _osl)
	{
		int tem = 0;
		for(OpticalService _os : _osl)
		{
			tem += _os.getNumberofFreeOTNs();
		}
		
		return tem;
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
			if(_ooo.type == OpticalService.CHANNEL_10G_FOT_OTN && _ooo.otn_children.size() < OpticalService.SUB_OTN_NUM )
			{
				if((_path != null && _ooo.path.equals(_path)) || _path == null)
					osl_return.add(_ooo);					
			}
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
