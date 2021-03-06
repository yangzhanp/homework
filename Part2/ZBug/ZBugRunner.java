import info.gridworld.actor.ActorWorld;
import info.gridworld.actor.Actor;//引用对象类
import info.gridworld.grid.Location;
import info.gridworld.grid.UnboundedGrid;
import java.util.Scanner;  

//import java.awt.Color;

public class ZBugRunner
{
    public static void main(String[] args)
    {
    	@SuppressWarnings("resource")
		Scanner src = new Scanner(System.in); 
    	UnboundedGrid<Actor> ans = new UnboundedGrid<Actor>();
    	ActorWorld world = new ActorWorld(ans);
    	
    	int len = src.nextInt();
        ZBug alice = new ZBug(len);//初始边长为一
        //alice.setColor(Color.ORANGE);
        //SpiralBug bob = new SpiralBug(2);
        world.add(new Location(17,17), alice);//初始位置坐标（17,17）刚好在画面中央
        //world.add(new Location(5, 5), bob);
        world.show();
    }
}