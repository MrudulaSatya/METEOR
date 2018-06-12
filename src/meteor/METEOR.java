/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meteor;

/**
 *
 * @author MRUDULA
 */


import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.sql.ResultSet;
import java.util.ArrayList;

public class METEOR{

    /**
     * @param args the command line arguments
     */
    
      
    public static void main(String[] args) {
        // TODO code application logic here
             
            //Database for reading the synonyms
            DBConn conn = new DBConn();
            conn.getConnection();
            conn.stmtConnection();
            
            //xml file reading for reference and candidate xml files
            readXML xml = new readXML();
            xml.readXMLfile();
            NodeList candList, refList;
            candList = xml.getCandList();
            refList = xml.getRefList();
            
            String[] splitC;
            String[] splitR1;
            
            ArrayList<String> splitCfrag = new ArrayList() ;
        
            String tempC ;
            int p1=0;
            int countspaceR =0, countspaceC=0;
            
            
            double meteor;
            double precision;
            double recall;
            int fragment = 0;
            double DF;
            double Fmean;
            double chunks;
            double avgLengthOfReferences,noOfwordsIncandi;
            int count = 0;
            int flagFrag = 0;
         
	
        // loop that reads the xml one by one  
	for (int tempcand = 0; tempcand < candList.getLength(); tempcand ++) { 

		Node candNode = candList.item(tempcand);
                Node refNode = refList.item(tempcand);

		if (candNode.getNodeType() == Node.ELEMENT_NODE && refNode.getNodeType() == Node.ELEMENT_NODE) {

			Element candElement = (Element) candNode;
                        Element refElement = (Element) refNode;

			String cand;
                        cand = candElement.getElementsByTagName("DATA").item(0).getTextContent();
                        
                        String R1; 
                        R1 = refElement.getElementsByTagName("Ref1").item(0).getTextContent();
                      
                         //Display values
                        System.out.println("cand: " + cand);
                        System.out.println("ref1: " + R1);
                       
                        // splitting the candidate and reference sentences into individual words
                        String temp = cand.replaceAll("[\\n]"," ");
                        splitC = temp.replaceAll("[.,!?:;'।]","").replaceAll("-"," ").split(" ");
                        
                        for(int vx=0;vx<splitC.length;vx++){
                             if("".equals(splitC[vx])){
                                countspaceC++;
                            }
                        }  
                        int lenCand = (splitC.length - countspaceC);
                        
                        temp = R1.replaceAll("\\n]"," ");
                        splitR1 = temp.replaceAll("[.,!?:;'।]","").replaceAll("-"," ").split(" ");
                         
                        for(int zx=0;zx<splitR1.length;zx++){
                            if("".equals(splitR1[zx])){
                                countspaceR++;
                            }
                         }
                        int lenRef = (splitR1.length - countspaceR) ;
                       
                        //calculating c for the current candidate sentence
                        noOfwordsIncandi = lenCand;
                        avgLengthOfReferences = lenRef ;
                        
                        //calculating p1
                        
                        //loop that traverses through each word of the candidate 
                        //one by one that is 1 gram precision
                        for(int i=0;i<lenCand;i++){
                            
                             //ith word stored in tempC 
                             tempC = splitC[i];
                             
                             //loop traverses R1 word by word
                             for(int j=0;j<lenRef;j++){
                                 
                                 //indication used later to 
                                 //see whether the word needs to be checked in R2
                                 //flagR2=0;
                                   
                                    if(tempC.equalsIgnoreCase(splitR1[j])){
                                         p1++; //word found in R1
                                         splitR1[j]="";//empty the word in R1 to avoid matching it again
                                         //flagR2=1;//no need to check in R2 for this word
                                         break;//comes out of the j loop
                                    }     
                              } //for for R1 ends 
                        }
                        
                        temp = cand.replaceAll("[\\n]"," ");
                        splitC = temp.replaceAll("[.,!?:;'।]","").replaceAll("-"," ").split(" ");
                        
                        temp = R1.replaceAll("\\n]"," ");
                        splitR1 = temp.replaceAll("[.,!?:;'।]","").replaceAll("-"," ").split(" ");
                         
                       
                        for(int i=0;i<lenCand;i++){
                            for(int j=0;j<lenRef;j++){
                                if(splitC[i].equalsIgnoreCase(splitR1[j])){
                                        splitCfrag.add(count, splitC[i]);
                                        splitR1[j]="";
                                        count++;
                                        break;
                                    }   
                            }
                        }
                        
                        //print the final calculated p1
                        System.out.println("No of words in candidate ="+ lenCand);
                        System.out.println("No of words in Refernce = "+lenRef);
                        System.out.print("\n No of matched words = " + p1  + "\n");          
  
                        precision = (double)p1/lenCand;
                        recall = (double)p1/lenRef;
                        System.out.println("precison = "+precision); 
                        System.out.println("recall = "+recall);
                        
                        Fmean = (double)(10*precision*recall)/(9*precision + recall);
                        System.out.println("Fmean = "+Fmean);
                        
                        temp = cand.replaceAll("[\\n]"," ");
                        splitC = temp.replaceAll("[.,!?:;'।]","").replaceAll("-"," ").split(" ");
                        
                        temp = R1.replaceAll("\\n]"," ");
                        splitR1 = temp.replaceAll("[.,!?:;'।]","").replaceAll("-"," ").split(" ");
                        
                        //Snippet calculating the number of fragments
                        int r = 0, i = 0, flag = 0, countMatch = 0; fragment = 0;
                        while(i<splitC.length){
                            if(splitR1[r].equalsIgnoreCase(splitC[i])){
                                flag = 1;
                                countMatch++;
                                r++;
                                i++;
                                if(countMatch>=1 && i==splitC.length-1){
                                    if(splitC[i-1].equalsIgnoreCase(splitR1[r-1]))
                                        fragment++;
                                }
                            }
                            else{
                                if(flag == 1 && countMatch>1){
                                    fragment++;
                                    r = 0;
                                    flag = 0;
                                    countMatch=0;
                                }
                                else{
                                    if(countMatch==1 && flag==1){
                                        i=i-1;
                                        countMatch =0;
                                        flag=0;
                                        if(i<0) i=0;
                                    }
                                    r++;
                                    if(r==splitR1.length){
                                        i++;
                                        r=0;
                                    }
                                } 
                            }
                        }
                        
                        System.out.println("No of Fragments = "+ fragment);
                        
                        chunks = ((double)(fragment - 1))/(p1 - 1);
                        System.out.println("No of Chunks = "+ chunks);
                                
                        DF = 0.5 * Math.pow(chunks,3); 
                        System.out.println("DF = "+ DF);
                        
                        meteor = Fmean * (1-DF);
                        System.out.println("Meteor Score = "+ meteor);
                        
                        
        } //if loop checking node type ends
                
               //resetting all values for next set of reference and candidate 
               p1 = 0; meteor = 0 ; precision = 0; recall=0; countspaceR=0;countspaceC=0; 
               chunks=0; DF=0; fragment=0; count = 0;
      }// for loop reading the xml file ends
        
  }//main ends
        
}//class ends
    
