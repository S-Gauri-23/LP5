import mpi.*;
import mpi.MPI;
import java.util.Arrays;
import java.util.Scanner;

public class ScatterGather{
    public static void main(String[] args) throws Exception{
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int unitsize = 3;
        int root = 0;
        int send_buffer[] = null;

        send_buffer = new int[unitsize * size];
        int receive_buffer[] = new int[unitsize];
        int new_receive_buffer[] = new int [size];

        if(rank == root){
            int total_elements = unitsize * size;
            System.out.println("Number of elements: " + total_elements);

            for(int i = 0; i < total_elements; i++){
                send_buffer[i] = i+1;
            }

            System.out.print("Array of elements: ");
            System.out.print(Arrays.toString(send_buffer));

            System.out.println("Number of Processes: " + size);
        }

        // Scatter data to processes
        MPI.COMM_WORLD.Scatter(
            send_buffer,
            0,
            unitsize,
            MPI.INT,
            receive_buffer,
            0,
            unitsize,
            MPI.INT,
            root
        );

        // Calculate sum at non root processes
        // Store result in first index of array

        for(int i = 1; i < unitsize; i++){
            receive_buffer[0] += receive_buffer[i];
        }
        System.out.println(
            "Intermediate sum at process " + (rank+1) + " is " + receive_buffer[0];
        )

        // Gather data from processes
        MPI.COMM_WORLD.Gather(
            receive_buffer,
            0,
            1,
            MPI.INT,
            new_receive_buffer,
            0,
            1,
            MPI.INT,
            root
        );

        //Aggregate output from all the processes

        if(rank == root){
            int total_sum = 0;

            for(int i = 0; i < size; i++){
                total_sum += new_receive_buffer[i];
            }
            System.out.println("Total Sum: " + total_sum);
        }
        MPI.Finalize();
    }
}

// export MPJ_HOME = /home..../mpj-vo_44
// export PATH = $MPJ_HOME/bin:$PATH
// javac -cp $MPJ_HOME/lib/mpj.jar ScatterGather.java
// $MPJ_HOME/bin/mpjrun.sh -np 4 Scatter gather