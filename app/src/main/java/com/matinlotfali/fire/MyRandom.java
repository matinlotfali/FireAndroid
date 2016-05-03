package com.matinlotfali.fire;

import java.util.Random;

/**
 * Created by Matin on 03/04/2016.
 */
class MyRandom
{
    int g_seed;

    public MyRandom(int seed){
        g_seed = seed;
    }

    public int nextInt(){
        g_seed = (214013*g_seed+2531011);
        return (g_seed>>16)&0x7FFF;
    }

    public int nextInt(int max)
    {
        g_seed = (214013*g_seed+2531011);
        int z = (g_seed>>16)&0x7FFF;
        if(z<0)
            z += Integer.MAX_VALUE;

        return (int)(z % max);
    }
}
