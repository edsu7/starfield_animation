import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.geom.Ellipse2D;

public class starfield extends java.lang.Object
{
    public static void main(String[] args)
    {
        JFrame frame = new JFrame (); //creates instance of a window
        frame.setSize(600,600);
        final RPanel rPanel = new RPanel(); //???????
        frame.add(rPanel);
        rPanel.setBackground(Color.BLACK);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                super.windowClosing(e);
                rPanel.stop();
                System.exit(0);
            }
        });
}
}

class RPanel extends JPanel{
    //SETTINGS
    private double mStarChance = 0.99; //from 0 to 1
        
    private float mTailWidth = 1;
    private int mRepaintTimeMS = 16; //?????
        
    //private Color mColor = new Color(White);
    //SETTINGS; NEEDS CLASSES
    
    private ArrayList<Star> starV; //in Java, arrays are fixed 
    //private ArrayList<Drop> dropV; //whilst arraylist are not
    private UpdateThread mUpdateThread;
    
    public RPanel() {
        starV = new ArrayList<>();
        //dropV = new ArrayList<>();
        
        mUpdateThread = new UpdateThread();
        mUpdateThread.start();
    }
    
    public void stop() {
        mUpdateThread.stopped=true;
    }
    
    public int getHeight(){
        return this.getSize().height;
    }
    public int getWidth(){
        return this.getSize().width;
    }
    private class UpdateThread extends Thread {
        public volatile boolean stopped = false; //atomic vs volatile
                                                 //i.e. how to evaluate conditions
        @Override
        public void run(){
            while(!stopped){
                RPanel.this.repaint();
                    try{
                        Thread.sleep(mRepaintTimeMS);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }
     @Override
     public void paintComponent(Graphics g){
         super.paintComponent(g);
         Graphics2D g2 = (Graphics2D) g;
         g2.setStroke(new BasicStroke(mTailWidth));
         g2.setColor(Color.WHITE);
         
         //DRAW Rain
         Iterator<Star> iterator = starV.iterator(); //iterates through elements
         while (iterator.hasNext()){
             Star star = iterator.next();
             star.update();
             star.draw(g2);
             
             if(star.y >=getHeight()||star.x >=getWidth()||star.y <0||star.x <0){
                 iterator.remove();
             }
         }
         //CREATE NEW Rain
         if (Math.random() < mStarChance){
             starV.add(new Star());
         }
         
     }
     
     class Star{
         float x;
         float y;
         float zx;
         float zy;
         float prevX;
         float prevY;
         //rain starts randomly
         public Star(){
             Random r = new Random();
             x = r.nextInt(getWidth());
             y = r.nextInt(getHeight());
             zx = 50; 
             zy = 50;
         }
         public void update(){
             prevX = x;
             prevY = y;
             
             if (x>=300)
             {
                 x+=((x-300)/zx);
             }
             else
             {
                 x-=((300-x)/zx);
             }
             if (y>=300)
             {
                 y+=((y-300)/zy);   
             }
             else
             {
                 y-=((300-y)/zy);
             }             
             
         }    
         
         public void draw(Graphics2D g2){
             //Line2D line = new Line2D.Double(x, y, prevX, prevY);
             //g2.draw(line);
             Ellipse2D circle = new Ellipse2D.Double(prevX, prevY, 5,5);
             g2.fill(circle);
             g2.draw(circle);
         }
     }
}