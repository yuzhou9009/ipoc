package edu.bupt.ipoc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.bupt.ipoc.constraint.Constraint;
import edu.bupt.ipoc.service.*;

public class VirtualTransLink extends Service{
	
	public static final int BUILD_REQUEST = 1;
	public static final int REMOVE_REQUEST = 2;
	public static final int EXTEND_REQUEST = 3;
	public static final int SHRINKED_REQUEST = 4;
	public static final int ADJUST_PSS_REQUEST = 5;
	//
	public static final int VTL_MAX_CARRIED_PS_HIGH = 10;
	public static final int VTL_MAX_CARRIED_PS_MID = 10;
	public static final int VTL_MAX_CARRIED_PS_LOW = 10;
	
	//test
	public static final int STATIC_AND_NOT_SHARED = 0;
	public static final int STATIC_BUT_SHARED = 1;
	public static final int DYNAMIC_AND_SHARED_BUT_CONFILICTING = 2;//traditional;vtl can be shared with other services, but once the bw is not enough, it will caused conflict, then the affiliated service need to move to other vtl or need a new vtl for being carried.
	public static final int VTL_BOD = 3;
	
	public static final int NOT_NEED_ADJUSTED = 0;
	public static final int NEED_TO_BE_ADJUSTED = 1;//No need to be extended but adjusted
	public static final int NEED_TO_BE_EXTENDED = 2;
	public static final int NEED_TO_BE_SHRINKED = 3;
	
	public static final double TH_USP_HIGH = 0.80;
	public static final double TH_USP_MID = 0.85;
	public static final double TH_USP_LOW = 0.95;
	
	//these four threshold values can be changed on-line. But these are used statically right now.
	public double th_usp_high = TH_USP_HIGH;
	public double th_usp_mid = TH_USP_MID;
	public double th_usp_low = TH_USP_LOW;	
	public double th_lsp_all = 0.4;
	public double th_step = 0.05;
	
	//
	public static final int BW_EMPTY = 0;
	
	public static final int NO_CORRECTION = 0;
	
	public int id;
	public int sourceVertex;
	public int destVertex;	
	
	public int vtl_priority = PRIORITY_LOW;
	
	public int max_carried_ps = VTL_MAX_CARRIED_PS_LOW;
	
	public int bw_capacity = BW_EMPTY;// the upper is 4Z bit, so int is enough right now.
	
	public int type  = STATIC_AND_NOT_SHARED;
	//public boolean bod_on = false;
	
	public int primary_service_bw_last = BW_EMPTY;
	public int primary_service_bw_current = BW_EMPTY;
	public int all_service_bw_current = BW_EMPTY;
	
	public List<PacketService> carriedPacketServices = null;
	
	public List<OTNService> relevantOTNServices = null;
	public List<OpticalService> relevantOpticalServices = null;
	
	public VirtualTransLink()
	{
		carriedPacketServices = new ArrayList<PacketService>();
		
		relevantOTNServices = new ArrayList<OTNService>();	
		relevantOpticalServices = new ArrayList<OpticalService>();		
	}
	
	public void setID(int _id)
	{
		this.id = _id;
	}
	
	public void setSourceAndDest(int _source, int _dest)
	{
		this.sourceVertex = _source;
		this.destVertex = _dest;
	}
	
	public void setPrioriy(int _priority)
	{
		//if(_priority < this.vtl_priority)//higher
		this.vtl_priority = _priority;
	}
	
	public void setType(int _type)
	{
		this.type = _type;
	}
	

	public void setTypeAccordingToConstraints(Map<Integer, Constraint> cons) {
		if(cons != null)
		{
			Constraint _con = cons.get(Constraint.VTL_CARRY_TYPE_C);
			if(_con != null)
			{
				setType(_con.value);
			}
		}
		else
		{
			System.out.println("The vtl carried type is not be defined.");
			setType(VirtualTransLink.STATIC_BUT_SHARED);
		}
	}
	
	public void addRelevantOpticalService(OpticalService _os)
	{
		this.relevantOpticalServices.add(_os);		
		this.updateCapacity();
	}
	
	public void addRelevantOTNService(OTNService _otns)
	{
		this.relevantOTNServices.add(_otns);		
		this.updateCapacity();
	}
	
	public int updateCapacity()
	{
		int _bw_capacity = 0;
		if(relevantOTNServices.size()>0)
		{
			for(OTNService otns : relevantOTNServices)
			{
				_bw_capacity += otns.getBWCapacity();
			}
		}
		
		if(relevantOpticalServices.size() > 0)
		{
			
			for(OpticalService os : relevantOpticalServices)
			{
				_bw_capacity += os.getBWCapacity();
			}
		}
		
		bw_capacity = _bw_capacity;
		return bw_capacity;		
	}
	
	public void updateBwStatistics()
	{
		primary_service_bw_last = primary_service_bw_current;
		primary_service_bw_current = getUsedBWofVTLByPrimaryPacketServices();
		all_service_bw_current = getUsedBWofVTLByAllPacketServices();
		updateCapacity();
	}
	
	public boolean canCarryMorePS()
	{
		if(this.carriedPacketServices.size() < this.max_carried_ps)
			return true;
		return false;
	}
	
	public double getCurrentHighThresholdValue()
	{
		if(vtl_priority == PRIORITY_HIGH)
			return th_usp_high;
		else if(vtl_priority == PRIORITY_MID)
			return th_usp_mid;
		else if(vtl_priority == PRIORITY_LOW)
			return th_usp_low;
		System.out.println("Big bug there shold have a priority for VTL!");
		return -1;
	}
	
	public static double getHighThresholdValue(int _priority)
	{
		if(_priority == PRIORITY_HIGH)
			return TH_USP_HIGH;
		else if(_priority == PRIORITY_MID)
			return TH_USP_MID;
		else if(_priority == PRIORITY_LOW)
			return TH_USP_LOW;
		System.out.println("Big bug there shold have a priority for VTL!");
		return -1;
	}
	
	public double getCurrentLowThresholdValue()
	{
		return this.th_lsp_all;
		/*
		if(vtl_priority == VTL_P_HIGH)
			return ;
		else if(vtl_priority == VTL_P_MID)
			return ;
		else if(vtl_priority == VTL_P_LOW)
			return ;
		System.out.println("Big bug there shold have a priority for VTL!");
		return -1;*/
	}
	
	public boolean canOfferMoreBW(int _bw)//just offer bw for the same priority
	{
		if(carriedPacketServices.size()>0)
		{
			if(this.type == VirtualTransLink.STATIC_AND_NOT_SHARED)
			{
				System.out.println("We may never have a chance to get here!");
				return false;
			}
			else if(this.type == VirtualTransLink.STATIC_BUT_SHARED)
			{
				if((getUsedBWofVTLByAllPacketServices() + _bw) * SURVIVABILITY_FACTOR <= getCapacity())
					return true;
			}
			else if(this.type == DYNAMIC_AND_SHARED_BUT_CONFILICTING)
			{
				if((getUsedBWofVTLByAllPacketServices() + _bw) * SURVIVABILITY_FACTOR <= getCapacity())
					return true;
			}
			else if(this.type == VirtualTransLink.VTL_BOD)
			{
				double _tem = 1.0/getCurrentHighThresholdValue();
				
				//TODO NEED MODIFY
				if(((getUsedBWofVTLByAllPacketServices() + _bw) / th_usp_low <= getCapacity()) 
						&& ((getUsedBWofVTLByPrimaryPacketServices()+ _bw) * _tem <= getCapacity()))
				{
					return true;
				}
			}
		}		
		return false;
	}
	
	public boolean canOfferMoreBwWithAdjustment(int _bw)
	{
		//TODO NEED MODIFY
		if((getUsedBWofVTLByAllPacketServices() + _bw) / getCurrentHighThresholdValue() <= getCapacity())
		{
			return true;
		}
		return false;
	}
	
	public int getRestBW()//ID:00 need to be modified
	{
		if(this.type == VirtualTransLink.VTL_BOD)
		{
			//TODO ID:00 
			return smallerValue((int)(getCapacity() * getCurrentHighThresholdValue()) - getUsedBWofVTLByPrimaryPacketServices(),
					(int)(getCapacity() * th_usp_low) - getUsedBWofVTLByAllPacketServices());
			//return (int)(getCapacity() * getCurrentHighThresholdValue()) - getUsedBWofVTLByAllPacketServices();
		}
		else if (this.type == VirtualTransLink.STATIC_AND_NOT_SHARED || this.type == VirtualTransLink.STATIC_BUT_SHARED || this.type == DYNAMIC_AND_SHARED_BUT_CONFILICTING)
			return (int)(getCapacity() * REGULAR_UPPER_THRESHOLD) - getUsedBWofVTLByAllPacketServices();
		else
			System.out.println("Never would be here!");
		return (int)(getCapacity() * REGULAR_UPPER_THRESHOLD) - getUsedBWofVTLByAllPacketServices();
	}
	
	public int getAcutallyRestBWforShare()
	{
		if(this.vtl_priority != Service.PRIORITY_LOW)
			return (((int)(getCapacity() * th_usp_low) - getUsedBWofVTLByAllPacketServices())/MIN_SHARED_BW_GRANULARITY)*MIN_SHARED_BW_GRANULARITY;
		else
			return getAcutallyRestBWforShareNoLimit();
	}
	
	public int getAcutallyRestBWforShareNoLimit()
	{
		return ((int)(getCapacity() * th_usp_low) - getUsedBWofVTLByAllPacketServices());
	}

	public int getCapacity()
	{
		updateCapacity();
		return this.bw_capacity;
	}
	
	public void removeRelevantOpticalService(OpticalService _os)
	{
		this.relevantOpticalServices.remove(_os);
		//this.allBW += _os.get
	}
	
	public void removeRelevantOTNService(OTNService _otns)
	{
		this.relevantOTNServices.remove(_otns);
		//this.allBW += _os.get
	}
	
	public void addPacketServiceToCarry(PacketService _ps)
	{
		this.carriedPacketServices.add(_ps);
	}
	
	public void removeCarriedPacketService(PacketService _ps)
	{
		this.carriedPacketServices.remove(_ps);
	}
	
	public int getUsedBWofVTLByAllPacketServices()
	{
		if(carriedPacketServices.size()>0)
		{
			int usedBW = 0;
			
			for(PacketService _ps : carriedPacketServices)
			{
				usedBW += _ps.getCurrentOccupiedBw();
			}
			return usedBW;
		}
		else
			return 0;
	}
	
	
	public int getUsedBWofVTLByPrimaryPacketServices()
	{
		if(carriedPacketServices.size()>0)
		{
			int usedBW = 0;
			
			for(PacketService _ps : carriedPacketServices)
			{
				if(_ps.priority == this.vtl_priority)
					usedBW += _ps.getCurrentOccupiedBw();
			}
			return usedBW;
		}
		else
			return 0;
	}
	
	public double getPathLong()
	{
		if(relevantOTNServices.size()>0)
		{
			return relevantOTNServices.get(0).osBelongTo.path.get_weight() + 5;
		}
		else if(relevantOpticalServices.size()>0)
		{
			return relevantOpticalServices.get(0).path.get_weight() + 5;
		}
		else
			return -1.0;
	}


	public int getCurrentStatue() 
	{
		// TODO Auto-generated method stub
		if(this.type == STATIC_AND_NOT_SHARED || this.type == STATIC_BUT_SHARED)
			return NOT_NEED_ADJUSTED; 
		
		else if(this.type == VTL_BOD)
		{
			if(isBeyondUpperThreshold(NO_CORRECTION))
			{
				//TODO logic need to be modified
				//if(this.vtl_priority == PRIORITY_HIGH || this.vtl_priority == PRIORITY_MID)
				return NEED_TO_BE_EXTENDED;
				//if(this.vtl_priority == PRIORITY_LOW)
					//return NEED_TO_BE_ADJUSTED;
			}
			else if(isUnderLowerThreshold(NO_CORRECTION))
			{
				if(circuitNumber() == 1)
				{
					//TODO System.out.println("This is a special conditions. May be extented!");
					//TODO If this is a service with low priority, we should do the adjustment.
					return NOT_NEED_ADJUSTED;
				}
				return NEED_TO_BE_SHRINKED;
			}
			else
			{
				if(isWholeBwBeyondUpperThreshold(NO_CORRECTION) || isStateChangingUpperThreshold(NO_CORRECTION))
				{
					return NEED_TO_BE_ADJUSTED;
				}
			}	
		}
		else if(type == DYNAMIC_AND_SHARED_BUT_CONFILICTING)
		{
			//TODO
		}			
		return NOT_NEED_ADJUSTED;
	}

	private boolean isBeyondUpperThreshold(int _correction) //only used after executing updateBwStatistics
	{
		if(type == VTL_BOD)
		{
			if(((getCapacity() + _correction) * getCurrentHighThresholdValue() - primary_service_bw_current) < 0)
				return true;
		}
		else
			System.out.println("Bug");
		//else if(type == DYNAMIC_AND_SHARED_BUT_CONFILICTING)
		//{
		//	if(getRestBW()<0)
		//		return true;
		//}
		return false;
	}

	private boolean isUnderLowerThreshold(int _correction) //only used after executing updateBwStatistics
	{
		if(type == DYNAMIC_AND_SHARED_BUT_CONFILICTING)
		{
			if((((getCapacity()+_correction) * th_lsp_all) - all_service_bw_current) >= 0)
				return true;
		}
		else if(type == VTL_BOD)
		{
			if((((getCapacity()+_correction) * th_lsp_all) - primary_service_bw_current) >= 0)
				return true;
		}
		return false;
	}

	private boolean isWholeBwBeyondUpperThreshold(int _correction) //only used after executing updateBwStatistics
	{
		if(type == DYNAMIC_AND_SHARED_BUT_CONFILICTING)
		{
			if(getRestBW()<0)
				return true;
		}
		else if(type == VTL_BOD)
		{
			if((((getCapacity()+_correction) * th_usp_low) - all_service_bw_current) < 0)
				return true;
		}
		return false;
	}
	
	private boolean isStateChangingUpperThreshold(int _correction)//only used after executing updateBwStatistics
	{
		if(type == VTL_BOD)
		{
			if((int)((1.0 * primary_service_bw_current/ bw_capacity)/th_step) != (int)((1.0 * primary_service_bw_last/ bw_capacity)/th_step))
				return true;
		}
		else
		{
			//TODO
			System.out.println("TODO");
		}
		return false;
	}
	
	public int howManyMoreBwNeeded() {
		// TODO Auto-generated method stub
		
		return (int) (BW_1G * getCurrentHighThresholdValue())-1;
	}
	
	public int circuitNumber()
	{
		int number = 0;
		if(this.relevantOpticalServices!= null)
			number += this.relevantOpticalServices.size();
		if(this.relevantOTNServices!= null)
			number += this.relevantOTNServices.size();
		return number;
	}
	
	public List<Service> servicesNeededToRemove() 
	{
		List<Service> ls = new ArrayList<Service>();
		//GET ALL CIRCUIT,		
		//max number of circuit needed to be removed
		//find the longest circuit/s.
		// TODO
		if(this.relevantOTNServices.size()>1)
			ls.add(this.relevantOTNServices.get(this.relevantOTNServices.size()-1));
		else if(this.relevantOpticalServices.size()>1)
		{
			//TODO
			//System.out.println("This should not happen Now! In servicesNeededToRemove");
		}
			
		return ls;
	}
	
	public void showMyself() 
	{
		System.out.println(this);	
	}
	
	public String toString()
	{
		String describtion = new String();
		describtion += "The vtl id:"+this.id;
		describtion += "\tthe source:"+this.sourceVertex+"\t the dest:"+this.destVertex;
		describtion += "\tthe priority:"+this.vtl_priority;
		describtion += "\n\tCarried ps num:"+carriedPacketServices.size();
		
		int request_bw = 0;
		for(PacketService _ps : carriedPacketServices)
		{
			describtion += "\n\t\t ps id:"+_ps.id+"\t the request bw is:"+_ps.getCurrentOccupiedBw();
			if(_ps.f_child == true)
				describtion += "\t it is a ps child, his father id:"+_ps.father_ps.id;
			request_bw += _ps.getCurrentOccupiedBw();
		}
		describtion += "\n\t\t all ps request bw is :"+request_bw;
		if(this.relevantOpticalServices.size()>0)
		{
			describtion += "\n\tUsed os num:"+relevantOpticalServices.size();
			for(OpticalService _os : relevantOpticalServices)
			{
				describtion += "\n\t\t os id:"+_os.id;
			}
		}
		if(this.relevantOTNServices.size()>0)
		{
			describtion += "\n\tUsed otn num:"+relevantOTNServices.size();
			if((relevantOTNServices.size()-1) * OTNService.BW_1G > request_bw)
				;//System.out.println("************************************************************");
			else if(relevantOTNServices.size() * OTNService.BW_1G < request_bw)
				System.out.println("!!!!!!!!!!!!!!!!!!!Bug");
			for(OTNService _otns : relevantOTNServices)
			{
				describtion += "\n\t\t otn id:"+_otns.id;
				describtion += "; carried os id:"+_otns.osBelongTo.id;
			}
		}
		
		return describtion;
		//System.out.println("vtl id:"+vtl.id+"\tthe priority:"+vtl.vtl_priority+"\tCarried packet services:"+vtl.carriedPacketServices.size());
		//System.out.println("")
	}
	
	public static int howManyOTNChannelsNeeded(int _priority, int _bw)
	{
		return (_bw / (int) (getHighThresholdValue(_priority) * Service.BW_1G + 1)) + 1;
	}
	
	public int smallerValue(int a, int b)
	{
		return a<b?a:b;
	}
}
