## ManyPush
1. Main Thread
  - MainActivity
    * onCreate
      - Initialize GLSurfaceView
      - Initialize GLRenderer
      - Attach renderer to surface
      - Display surface
  - GLSurfaceView
    * onTouch
      Motion Event Switch:
      - Down
      -	Secondary Down
      -	Move
      -	Secondary Up
      - Up
2. Rendering Thread
  - GLRenderer
    * onSurfaceCreated
      - Set background color
      - Set camera position, direction, and direction of up.
      - Write shader programs as strings, then link and pass into OpenGL
      - Store handles to resulting position, color, and final mvp matrices
    * onSurfaceChanged
      - Store new screen dimensions
      - Create appropriate projection matrix
    * onDrawFrame
      - Make the vertex buffer and pass it to the draw method
    * drawTriangle
      - Load the position and color information through their handles
      - Load the model matrix and use the view and projection matrices to make the final mvp matrix
      - Attach resulting matrix using its handle and call OpenGL to draw it
