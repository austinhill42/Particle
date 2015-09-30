## ManyPush
1. Main Thread
  - MainActivity
    * onCreate
      1. Initialize GLSurfaceView
      2. Initialize GLRenderer
      3. Attach renderer to surface
      4. Display surface
  - GLSurfaceView
    * onTouch
      - Motion Event Switch:
        - Down
        -	Secondary Down
        -	Move
        -	Secondary Up
        - Up
2. Rendering Thread
  - GLRenderer
    * onSurfaceCreated
      1. Set background color
      2. Set camera position, direction, and direction of up.
      3. Write shader programs as strings, then link and pass into OpenGL
      4. Store handles to resulting position, color, and final mvp matrices
    * onSurfaceChanged
      1. Store new screen dimensions
      2. Create appropriate projection matrix
    * onDrawFrame
      1. Make the vertex buffer and pass it to the draw method
    * drawTriangle
      1. Load the position and color information through their handles
      2. Load the model matrix and use the view and projection matrices to make the final mvp matrix
      3. Attach resulting matrix using its handle and call OpenGL to draw it
