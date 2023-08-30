import java.io.*;
public class Reversible_Booths
{
    static int AC,count,Q1;		//AC denotes ACCUMULATOR, Q1 denotes Q(-1) of BoothsAlgo
    static int Q[], A[], M[];		//Q,A,M denotes Multiplier,Array,Multiplicand of BoothsAlgo respectively
    static final int N=64;		//Bit Architecture
    Reversible_Booths() {
	Q= new int [N];		
	A= new int [N];
	M= new int [N];
	count=N;			//counter set to N-bit
	Q1=0;				//Initialized Q1 to 0
    }

//Function to clear ACCUMULATOR
    void CLR() {
        AC=AC^AC;
    }

//This func takes a integer input and returns Twos_COMP(input) through array format
//it first checks if the input is +ve or not
// if +ve it simply convert it into binary format storing in array
//   as for +ve no. binary is equivalent to Twos_COMP
//if -ve it first makes the number +ve by multiplying it by -1
//   then it converts the +ve value to binary storing in array
//	then it takes out bitwise Ones_COMP of array value and adds 1 stored in temp to the array
// 	   to make the equivalent Twos_COMP of the -ve input  

    int[] twosComp(int input) { 
    	int array[] = new int [N];	//Array is used to calculate the Twos_COMP(input)
	int temp[]= new int [N];	// AUX array used to store value 1
	temp[N-1]=1;
	int number,i;			//number used to store input and i used for index
	number=input;
	i=N-1;
	if(number>=0){			//Check if +ve
	while(number!=0){
		array[i--]=array[i]^(number%2);	//Binary Conversion
		number=number/2;
	}
        }else {				//Otherwise -ve
		number=(-1)*number;	//Makes +ve
		while(number!=0){
		array[i--]=array[i]^number%2;  //Binary Conversion
		number=number/2;
		}
		
		for(int j=0;j<N;j++) {	       //Bitwise Ones_COMP
			if(array[j]==0)
				array[j]=1;	
			else
				array[j]=0;
		}
		array=ADD(array,temp);	//adds 1 to the Ones_COMP to make Twos_COMP
	}
	
	return array;
    }

//Load the ACCUMULATOR with input integer and return Twos_COMP(AC) 
    int[] LDM(int input) {
           	CLR();
		AC=AC^input;    
		return twosComp(AC);
        
    }

//Addition bitwise using the logic of CLA ADDER

    int[] ADD(int x[], int y[]) {
	int C[]= new int[N];                   //Carry Array
	int SUM[]= new int[N];		       //SUM Array
	C[N-1]=C[N-1]^C[N-1];                    //CarryIn of LSB bit set to 0
	for(int i=N-1;i>=0;i--) {
		SUM[i]=SUM[i]^(x[i]^y[i]^C[i]);  //CLA SUM
		if(i!=0)			//Otherwise C[0-1] gives RangeOutOfBound
			C[i-1]=C[i-1]^((x[i]*y[i])|(y[i]*C[i])|(x[i]*C[i])); // CLA CarryOUT
	}
	return SUM;
    }

//Subtraction bitwise using Addition 
//A-B >> A+(-B) .. -B -->Twos_COMP(B) 

    int[] revSUB(int x[], int y[]) {
	 int temp1[]=new int[N];      //AUX array to store value 1
	 int temp2[]=new int[N];      //AUX array to store Twos_COMP(y)
	 int DIFF[]=new int[N];       //Array to store the Difference
	 temp1[N-1]=1;		      //AUX array set to 1
	 for(int i=0;i<N;i++) {
		temp2[i]=not(y[i]);   //AUX array gets Ones_COMP(y)
	 }
	 temp2=ADD(temp2,temp1);      //AUX array gets -B
	 DIFF=ADD(x,temp2);	      //AUX array gets A+(-B)
         return DIFF;
    }


//Right shift function
//Right shifting (A,Q,Q1)

    void rightShift() {
	int arr1[]= new int[N];		// AUX array to store Shifted Q values
	int arr2[]= new int[N];		// AUX array to store Shifted A values
	for(int i=0;i<N;i++) {
		arr2[i]=arr2[i]^arr2[i];	//Clear arr2[i]
		arr2[i]=arr2[i]^A[i];		//arr2[i]=A[i]
		A[i]=A[i]^A[i];			//Clear A[i]
		arr1[i]=arr1[i]^arr1[i];	//Clear arr1[i]
		arr1[i]=arr1[i]^Q[i];		//arr1=Q[i]
		Q[i]=Q[i]^Q[i];			//Clear Q[i]
	}
	for(int i=0;i<N-1;i++) { 		//Right Shifting of A and Q
		A[i+1]=A[i+1]^arr2[i];
		Q[i+1]=Q[i+1]^arr1[i]; 
	}
	A[0]=A[0]^arr2[0];			//Set MSB of A to previous value of MSB stored in arr2
	Q[0]=Q[0]^arr2[N-1];			//Set MSB of Q to LSB of A stored in arr2
	Q1=Q1^Q1;				//Clear Q1
	Q1=Q1^arr1[N-1];				//Set Q1 to LSB of Q stored in arr1
	System.out.println("Right Shifted: ");
	display();				// Displaying A,Q,Q1
	System.out.print("\nQ1: "+Q1+"\n");
    }


//Function to do the booth's multiplication using reversible modules	
    void multiply() {
	int X[]= new int [N];		//Array to store A
	int temp[]= new int[N];		//Array to store result of revSUB and ADD
	while(count>0) {
		System.out.println("\nCount: "+count);
		if( (Q[N-1]==1) && (Q1==0) ) {	//if Q0Q-1 == 10
		    System.out.println("\nQ[0]Q1=10");
			for(int i=0;i<N;i++) {	//Cut A to X
				X[i]=X[i]^X[i];
				X[i]=X[i]^A[i];
				A[i]=A[i]^A[i]; //clr()
			}
			temp=revSUB(X,M);	//Perform A-M
			for(int i=0;i<N;i++) 
				A[i]=A[i]^temp[i];	//Storing SUB to A
			rightShift();		// rightShift(A,Q,Q1)
			count--;		//Decrease count value by 1
			continue;		
		} 
	
		if( (Q[N-1]==0) && (Q1==1) ) {	//if Q0Q-1 == 01
			System.out.println("\nQ[0]Q1=01");
			for(int i=0;i<N;i++) {
				X[i]=X[i]^X[i];
				X[i]=X[i]^A[i];
				A[i]=A[i]^A[i];
			}
			temp=ADD(X,M);		//Perform A+M
			for(int i=0;i<N;i++) 
				A[i]=A[i]^temp[i];
			rightShift();
			count--;
			continue;
		} 
	
		if( ((Q[N-1]==0) && (Q1==0)) || ((Q[N-1]==1) && (Q1==1)) ) { //if Q0Q-1 == 00 or 11
			System.out.println("\nQ[0]Q1=00 or 11");
			rightShift();
			count--;
		} 
     	}
     }

//Function returns bitwise complement>> if 0 returns 1 else returns 0
  int not(int x) {
		if(x==0)
			return 1;
		else	
			return 0;
     }

//Function to display contents of A,Q
   void display() {
	System.out.println("\nAQ: ");
	for(int i=0;i<N;i++) 
		System.out.print(A[i]);
	for(int i=0;i<N;i++) 	
		System.out.print(Q[i]);
     }

    public static void main(String[] args) throws IOException{
	Reversible_Booths obj = new Reversible_Booths();
	String input;
	int arr[] = new int[N];
        try {
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        	obj.CLR();

		for(int i=0;i<N;i++)
			Q[i]=Q[i]^Q[i]; //  *** to be used by clr()
        	System.out.println("Enter the Multiplier: ");
	        input=br.readLine();
		arr=obj.LDM(Integer.parseInt(input));
		System.out.print("\nQ: ");
		for(int i=0;i<N;i++) {
			Q[i]=Q[i]^arr[i];
	        	System.out.print(Q[i]);
        	}
		obj.CLR();
	
		System.out.println("\nEnter Multiplicand: ");       
		input=br.readLine();
		arr=obj.LDM(Integer.parseInt(input));
		System.out.print("\nM: ");
		for(int i=0;i<N;i++) {
			M[i]=M[i]^M[i];
			M[i]=M[i]^arr[i];
	        	System.out.print(M[i]);
        	}
		Q1=Q1^Q1;
		obj.multiply();
		obj.display();
		} catch(Exception e) {
        	    e.printStackTrace();
        	} 
	System.out.println("\nCompleted Sucessfully!");	
     }
}
