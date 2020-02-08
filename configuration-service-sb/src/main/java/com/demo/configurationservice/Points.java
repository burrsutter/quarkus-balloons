package com.demo.configurationservice;

public class Points {
  private int red;
  private int yellow;
  private int green;
  private int blue;
  private int goldenSnitch1;
  private int goldenSnitch2;

  public Points(
      int red, 
      int yellow,
      int green,
      int blue,
      int goldenSnitch1,
      int goldenSnitch2) {
        this.red = red;
        this.yellow = yellow;
        this.green = green;
        this.blue = blue;
        this.goldenSnitch1 = goldenSnitch1;
        this.goldenSnitch2 = goldenSnitch2;

  }
  public int getRed() {
    return red;
  }

  public int getYellow() {
    return yellow;
  }

  public int getGreen() {
    return green;
  }

  public int getBlue() {
    return blue;
  }

  public int getGoldenSnitch1() {
    return goldenSnitch1;
  }

  public int getGoldenSnitch2() {
    return goldenSnitch2;
  }

  public void setRed(int red) {
    this.red = red;
  }

  public void setYellow(int yellow) {
    this.yellow = yellow;
  }

  public void setGreen(int green) {
    this.green = green;
  }

  public void setBlue(int blue) {
    this.blue = blue;
  }

  public void setGoldenSnitch1(int goldenSnitch1) {
    this.goldenSnitch1 = goldenSnitch1;
  }

  public void setGoldenSnitch2(int goldenSnitch2) {
    this.goldenSnitch2 = goldenSnitch2;
  }
  
}

/*
	"points": {
		"red": 1,
		"yellow": 1,
		"green": 1,
		"blue": 1,
		"goldenSnitch1": 100,
		"goldenSnitch2": 100
	}
*/