
public class AddressToReg {

	public String addressToReg(String address) {
	    if (address.contains("��õ")) {
	    	return "2800000000";
	    }else if (address.contains("������")) {
	    	return "4200000000";
        }else if (address.contains("��⵵")) {
            return "4100000000";
        }else if (address.contains("��󳲵�")) {
	    	return "4800000000";
        }else if (address.contains("���ϵ�")) {
        	return "4700000000";
        }else if (address.contains("����")) {
	    	return "2900000000";
        }else if (address.contains("�뱸")) {
        	return "2700000000";
        }else if (address.contains("����")) {
	    	return "3000000000";
        }else if (address.contains("�λ�")) {
        	return "2600000000";
        }else if (address.contains("����")) {
	    	return "1100000000";
        }else if (address.contains("����")) {
        	return "3611000000";
        }else if (address.contains("���")) {
	    	return "3100000000";
        }else if (address.contains("���󳲵�")) {
        	return "4600000000";
        }else if (address.contains("����ϵ�")) {
        	return "4500000000";
        }else if (address.contains("����")) {
	    	return "5000000000";
        }else if (address.contains("��û����")) {
        	return "4400000000";
        }else if (address.contains("��û�ϵ�")) {
        	return "4300000000";
	    }
	    return ""; 
	}
}