// breakout.c

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

// Stanford Portable Library
#include <spl/gevents.h>
#include <spl/gobjects.h>
#include <spl/gwindow.h>

#define _XOPEN_SOURCE
// height and width of game's window in pixels
#define HEIGHT 600
#define WIDTH 400

#define PWIDTH 68
#define PHEIGHT 10

// number of rows and columns of bricks
#define ROWS 5
#define COLS 10

// radius of ball
#define RADIUS 10

// lives
#define LIVES 3



// prototypes
void initBricks(GWindow window);
GOval initBall(GWindow window);
GRect initPaddle(GWindow window);
GLabel initScoreboard(GWindow window);
void updateScoreboard(GWindow window, GLabel label, int points);
GObject detectCollision(GWindow window, GOval ball);

int main(void)
{
    // seed pseudorandom number generator
    srand48(time(NULL));

    // instantiate window
    GWindow window = newGWindow(WIDTH, HEIGHT);

    // instantiate bricks
    initBricks(window);

    // instantiate ball, centered in middle of window
    GOval ball = initBall(window);

    // instantiate paddle, centered at bottom of window
    GRect paddle = initPaddle(window);
    

    // instantiate scoreboard, centered in middle of window, just above ball
    GLabel label = initScoreboard(window);

    // number of bricks initially
    int bricks = COLS * ROWS;

    // number of lives initially
    int lives = LIVES;

    // number of points initially
    int points = 0;
    
    int gameover = 0;
    int restart = 0;

    // keep playing until game over
    
    
    while (lives > 0 && bricks > 0)
    {
        printf("%i\n", lives);
        double velocityx = 1 + drand48();
        if ((drand48() * 50001) > 25000)
        {
            velocityx = -velocityx + drand48();
        }
        
        double velocityy = -3;
        
        while (restart == 0 && lives > 0)
        {
        
            move(ball, velocityx, velocityy);
            pause(10);
            updateScoreboard(window, label, points);
            GObject object = detectCollision(window, ball);
                 
            if (object == paddle | object != NULL && strcmp(getType(object), "GRect") == 0)
            {
                velocityy = -velocityy;
            
                if ( object != paddle && object != NULL && strcmp(getType(object), "GRect") == 0)
                {
                    removeGWindow( window, object);
                    bricks--;
                    points++;
                }
            }
                
            // bounce off right edge of window
            if (getX(ball) + getWidth(ball) >= getWidth(window))
            {
                velocityx = -velocityx;
            }

            // bounce off left edge of window
            else if (getX(ball) <= 0)
            {
                velocityx = -velocityx;
            }
            
            if (getY(ball) + getHeight(ball) >= getHeight(window) && lives == 1)
            {
               
                gameover = 1;
                lives = lives - 1;
                printf("other bottom hit function\n");
            }
            
            // bounce off bottom edge of window
            if (getY(ball) + getHeight(ball) >= getHeight(window))
            {
               
                gameover = 1;
            }

            // bounce off top edge of window
            else if (getY(ball) <= 0)
            {
                velocityy = -velocityy;
            }
            
            GObject detectCollision(GWindow window, GOval ball);
            
            // check for mouse event
            GEvent event = getNextEvent(MOUSE_EVENT);
            
            if (points == 50)
            {
                waitForClick();
                closeGWindow(window);
                return 0;
            }
                    
            // if we heard one
            if (event != NULL && lives > 0)
            {
                // if the event was movement
                if (getEventType(event) == MOUSE_MOVED)
                {
                    // ensure paddle follows top cursor
                    double x = getX(event) - getWidth(paddle) / 2;
                    double y = HEIGHT - 25;
                    setLocation(paddle, x, y);
                }
                
                if (getEventType(event) == MOUSE_CLICKED && gameover == 1)
                {
                    
                    setLocation(ball, WIDTH / 2, HEIGHT / 2);
                    gameover = 0;
                    lives = lives - 1;
                    restart = 1;
                   
                }
            }
        }
    
        if (lives > 0)
        {
            restart = 0; 
        }
        
    }

    // wait for click before exiting
    waitForClick();


    // game over
    closeGWindow(window);
    return 0;
}

/**
 * Initializes window with a grid of bricks.
 */
void initBricks(GWindow window)
{
    int counter = 0;
    int x = 3;
    int y = 50;
    GRect squares[ROWS * COLS];
    char* colors[] = {"BLUE", "CYAN", "GREEN", "MAGENTA", "ORANGE", "PINK", "RED", "YELLOW"};
    
    for (int i = 0; i < COLS; i++)
    {
        y = 50;
        for (int j = 0; j < ROWS; j++)
        {   
            
            
            squares[i] = newGRect( x, y, PWIDTH / 2, PHEIGHT);
            setColor(squares[i], colors[((int) (drand48() * 1000)) % 8]);
            setFilled(squares[i], true);
            add(window, squares[i]);
            y = y + 15;
        }
          
        x = x + 40;
    }
}


/**
 * Instantiates ball in center of window.  Returns ball.
 */
GOval initBall(GWindow window)
{
    GOval ball = newGOval(WIDTH / 2, HEIGHT / 2, 20, 20);
    setColor(ball, "BLACK");
    setFilled(ball, true);
    add(window, ball);
    return ball;
}

/**
 * Instantiates paddle in bottom-middle of window.
 */
GRect initPaddle(GWindow window)
{
    GRect paddle = newGRect((WIDTH / 2) - (PWIDTH / 2), HEIGHT - 25, PWIDTH, PHEIGHT);
    setColor(paddle, "BLACK");
    setFilled(paddle, true);
    add(window, paddle);
    return paddle;
}

/**
 * Instantiates, configures, and returns label for scoreboard.
 */
GLabel initScoreboard(GWindow window)
{
    GLabel label = newGLabel("");
    setFont(label, "SansSerif-50");
    setColor(label, "lightGray");
    add(window, label);
    return label;
}

/**
 * Updates scoreboard's label, keeping it centered in window.
 */
void updateScoreboard(GWindow window, GLabel label, int points)
{
    // update label
    char s[12];
    sprintf(s, "%i", points);
    setLabel(label, s);

    // center label in window
    double x = (getWidth(window) - getWidth(label)) / 2;
    double y = (getHeight(window) - getHeight(label)) / 2;
    setLocation(label, x, y);
}

/**
 * Detects whether ball has collided with some object in window
 * by checking the four corners of its bounding box (which are
 * outside the ball's GOval, and so the ball can't collide with
 * itself).  Returns object if so, else NULL.
 */
GObject detectCollision(GWindow window, GOval ball)
{
    // ball's location
    double x = getX(ball);
    double y = getY(ball);

    // for checking for collisions
    GObject object;
   // check for collision at ball's bottom-left corner
    object = getGObjectAt(window, x, y + 2 * RADIUS);
    if (object != NULL)
    {
        return object;
    }

    // check for collision at ball's top-left corner
    object = getGObjectAt(window, x, y);
    if (object != NULL)
    {
        return object;
    }

    // check for collision at ball's top-right corner
    object = getGObjectAt(window, x + 2 * RADIUS, y);
    if (object != NULL)
    {
        return object;
    }


    // check for collision at ball's bottom-right corner
    object = getGObjectAt(window, x + 2 * RADIUS, y + 2 * RADIUS);
    if (object != NULL)
    {
        return object;
    }

    // no collision
    return NULL;
}
