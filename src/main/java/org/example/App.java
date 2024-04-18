package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class App  {



    int a = 0;
    static class A implements Cloneable
    {

        int i;

        String s;

        public A(int i, String s) {
            this.i = i;
            this.s = s;
        }

        A m (){
            return new A(1,"1");
        }
        public static A m2(){
            return new A(2,"2");
        }

        public  void qe(App o){
            o = new App();
            System.out.println(o);
        }

        public  void qe(Object o){
            System.out.println(o);
            System.out.println(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true; // ссылается на
            if (o == null || getClass() != o.getClass()) return false;
            A a = (A) o;
            return i == a.i && Objects.equals(s, a.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, s);
        }
    }


    public static void main(String[] args) throws CloneNotSupportedException {

        A a1 =  new A(1,"1");
        A a2 =  a1;
        A a3 =  a2;

        System.out.println(a1);
        System.out.println(a2);
        System.out.println(a3 == a1);

//        System.out.println(a1);
//
//        a1.qe(a1);
//        App aP = new App();
//
//        System.out.println(aP);
//        a1.qe(aP);
//        System.out.println(aP);







        // eq
        // hash
        // tos
        // wait
        // notify
        // notifAll
        // clone
        // getClass
        // finalize


    }





    }



