/*
/*
import java.awt.*;     
import java.applet.*;
import java.lang.*;
import java.util.Random;

class star
  { int height, distance, x;
    star (int h, int d, int x1)
      { height=h;
        distance = d;
        x = x1;
      }
  // we can reposition a star by defining a new height and distance only
    void update_star (int h, int d, int x1)
      { height = h;
        distance = d;
        x = x1;
      }
   }
   
public class starfield extends java.applet.Applet implements Runnable
  { String s = null;
    Random r = new Random();
    final int numstars = 150;
    int maxDepth = 256, maxColorDepth=256;
    star mystars[] = new star[numstars];  // We may make this a parameter later
    Thread threadOne = null;
    int semiCycles = 0;
    
    int arrayCount;
    int speed, numColors, numDepths;      // parameters that can be passed down to app.
    final int windowDivisor = 30;
    int windowlimitx;
    int depthadj;
    int depth[];    
    Color bgc = Color.black;
    Color starcolors[] = new Color[maxColorDepth];   // Maximum number of different colors we can generate

    Image offScrImage;  // This allows us to instantiate a graphics object
    Graphics offScrGC;  // Which we can use to double buffer the screen

/* Here we initialize the parameters passed down to the applet, construct an initial star
   field, and generate the appropriate movement and illuminance functions.
*//*
    public void init()
      { int colorVal, colorStep, indx;
        windowlimitx = this.size().width * windowDivisor;
        s = getParameter("speed");        // Check if user specified refresh rate (in msec)
        if (s == null) speed = 50;            // If not, set refresh to 50/100 sec
        else speed = Integer.parseInt(s);

        s = getParameter("numColors");           // Check if user specified number of colors
        if (s == null) numColors = 256;          // If not, set to full depth of 256 colors
        else numColors = Integer.parseInt(s);
        if (numColors < 1) numColors = 1;
        else if (numColors > maxDepth) numColors = maxDepth;
        
        s = getParameter("numDepths");           // Determine the number of depth levels in field
        if (s == null) numDepths = maxDepth;     // If not specified, set to 256 depth levels
        else numDepths = Integer.parseInt(s);
        if (numDepths < 2) numDepths = 2;
        else if (numDepths > maxDepth) numDepths = maxDepth;
        depthadj = (maxDepth / numDepths) - 1;
        
        s = getParameter("cycles");         // Really semi-cycles
        if (s == null) semiCycles = 0;
        else semiCycles = Integer.parseInt(s);
        // If semiCycles is > 0, we use a different method to compute motion, and don't exploit
        // depth information.  Construct array sufficient to cover screen
        
        if (semiCycles > 0)
          { maxDepth = this.size().width;
            numDepths = 2;
          }
        depth = new int[maxDepth];
        for (arrayCount=0; arrayCount < numstars; arrayCount++)
            { mystars[arrayCount] = new star (Math.abs(r.nextInt()) % this.size().height,
              (Math.abs(r.nextInt()) % numDepths) + depthadj,
                Math.abs(r.nextInt()) % windowlimitx);
             }
        if (semiCycles == 0)
            for (arrayCount=0; arrayCount < maxDepth; arrayCount++)
                { depth[arrayCount] = maxDepth - arrayCount;
                }
        else
            for (arrayCount=0; arrayCount < maxDepth; arrayCount++)
                { depth[arrayCount] =  
                    (int) (60.0 + 29.0 * Math.sin((double) (arrayCount * semiCycles * Math.PI) / maxDepth));
                }
        /* Because of various Gamma properties and human sensitivity to light, we are going
           to assign small color ranges differently than the full possible range.  The mean
           value is supposed to be about 192.
        *//*
        if (numColors < 2)
            { for (indx = 0; indx < maxColorDepth; indx++) starcolors[indx] =
                    new Color(192, 192, 192);
            }
        else if (numColors < 20) // Use range of 128 to 255, mean value 192;
          { colorVal = 255; colorStep = (int) ((128.0 / (float) numColors) + 0.5);
//          System.out.println("numColors: " + numColors + " colorStep: " + colorStep);
            arrayCount = 0;
            while (arrayCount < maxColorDepth)
              { for (indx = 0; indx < maxColorDepth/numColors && arrayCount < maxColorDepth; indx++)
                    { starcolors[arrayCount] = new Color(colorVal, colorVal, colorVal); arrayCount++;
                    }
                colorVal -= colorStep;
                if (colorVal < 1) colorVal = 1;
              }
          }
        else
          { colorVal = 255; colorStep = ((int) (((float) maxColorDepth / (float) numColors) + 0.5));
//          System.out.println("numColors: " + numColors + " colorStep: " + colorStep);
            arrayCount = 0;
            while (arrayCount < maxColorDepth)
              { for (indx = 0; indx < maxColorDepth/numColors && arrayCount < maxColorDepth; indx++)
                    { starcolors[arrayCount] = new Color(colorVal, colorVal, colorVal); arrayCount++;
                    }
                colorVal -= colorStep;
                if (colorVal < 1) colorVal = 1;
              }
          }
        offScrImage = createImage(this.size().width,this.size().height);
        offScrGC = offScrImage.getGraphics();
      }

    public void start()
      { if(threadOne == null) 
        { threadOne = new Thread(this);
              threadOne.start();
        }
      }

    public void stop()
        { if (threadOne != null)
             { threadOne.stop();
                threadOne = null; 
             }
        }

    public void run()
      { while (threadOne != null) {
        try {Thread.sleep(speed);} catch (InterruptedException e){}
            repaint();
         }
        threadOne = null;
      }

/* This applet does all of its work in the paint routine, at least for now!
   User interface? Who needs one!
*//*
    public void paint(Graphics g)
     { offScrGC.setColor(bgc);
       offScrGC.fillRect(0,0,this.size().width,this.size().height);
//       offScrGC.setColor(Color.white);
//       System.out.println("In paint! numstars: " + numstars + " mystars[0].x " + mystars[0].x);
       for (arrayCount = 0; arrayCount < numstars; arrayCount++)
        {   offScrGC.setColor(starcolors[mystars[arrayCount].distance]);
            offScrGC.drawRect (mystars[arrayCount].x / windowDivisor,
                mystars[arrayCount].height, 1, 1);
            if (semiCycles == 0)
              { mystars[arrayCount].x += depth[mystars[arrayCount].distance];
              }
            else mystars[arrayCount].x += depth[(int) (mystars[arrayCount].x / windowDivisor)];
            if (mystars[arrayCount].x >= windowlimitx)
                    mystars[arrayCount].update_star((Math.abs(r.nextInt())) % this.size().height,
                    (Math.abs(r.nextInt()) % numDepths) + depthadj, mystars[arrayCount].x - windowlimitx);
              
            /* offScrGC.drawString("depth of star " + arrayCount + " is " + mystars[arrayCount].distance,
                    1, arrayCount * 8); */
                        
      /*  }
       g.drawImage(offScrImage,0,0,this);

     }
/* Here we overload the standard update function to provide one that does not blank the
   screen over and over.  Since we create an image of the screen in OffScrGC, we can just
   blit the material over to the normal canvas.  Now if I could just figure out how to use
   clipRect properly...
*/
/*
public void update(Graphics g) 
  { paint(g);}
}
*/