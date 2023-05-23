
public class AddressToReg {

	public String addressToReg(String address) {
	    if (address.contains("인천")) {
	    	return "2800000000";
	    }else if (address.contains("강원도")) {
	    	return "4200000000";
        }else if (address.contains("경기도")) {
            return "4100000000";
        }else if (address.contains("경상남도")) {
	    	return "4800000000";
        }else if (address.contains("경상북도")) {
        	return "4700000000";
        }else if (address.contains("광주")) {
	    	return "2900000000";
        }else if (address.contains("대구")) {
        	return "2700000000";
        }else if (address.contains("대전")) {
	    	return "3000000000";
        }else if (address.contains("부산")) {
        	return "2600000000";
        }else if (address.contains("서울")) {
	    	return "1100000000";
        }else if (address.contains("세종")) {
        	return "3611000000";
        }else if (address.contains("울산")) {
	    	return "3100000000";
        }else if (address.contains("전라남도")) {
        	return "4600000000";
        }else if (address.contains("전라북도")) {
        	return "4500000000";
        }else if (address.contains("제주")) {
	    	return "5000000000";
        }else if (address.contains("충청남도")) {
        	return "4400000000";
        }else if (address.contains("충청북도")) {
        	return "4300000000";
	    }
	    return ""; 
	}
}