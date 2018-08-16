import tensorflow as tf
import os
from tensorflow.python.tools import inspect_checkpoint as chkp

SAVE_PATH = '/home/k33p/Downloads/mask_rcnn_inception_v2_coco_2018_01_28/'
MODEL_NAME = 'model'
VERSION = 1
SERVE_PATH = './serve/{}/{}'.format(MODEL_NAME, VERSION)

checkpoint = tf.train.latest_checkpoint(SAVE_PATH)

tf.reset_default_graph()
chkp.print_tensors_in_checkpoint_file(checkpoint, tensor_name='', all_tensors=True, all_tensor_names=True)

with tf.Session() as sess:
    # import the saved graph
    saver = tf.train.import_meta_graph(checkpoint + '.meta')
    # get the graph for this session
    graph = tf.get_default_graph()
    sess.run(tf.global_variables_initializer())
    chkp.print_tensors_in_checkpoint_file(checkpoint + ".ckpt", tensor_name='', all_tensors=True, all_tensor_names=True)

    # get the tensors that we need  
    new_saver = tf.train.import_meta_graph('my_test_model-1000.meta')
    new_saver.restore(sess, tf.train.latest_checkpoint('./'))
    inputs = graph.get_tensor_by_name('image_tensor:0')
    predictions = graph.get_tensor_by_name('detection_masks:0')
    saver = tf.train.Saver()
    save_path = saver.restore(sess, checkpoint + '.ckpt')
    # create tensors info
    model_input = tf.saved_model.utils.build_tensor_info(inputs)
    model_output = tf.saved_model.utils.build_tensor_info(predictions)
    
    # build signature definition
    signature_definition = tf.saved_model.signature_def_utils.build_signature_def(
        inputs={'inputs': model_input},
        outputs={'outputs': model_output},
        method_name= tf.saved_model.signature_constants.PREDICT_METHOD_NAME)

    builder = tf.saved_model.builder.SavedModelBuilder(SERVE_PATH)

    builder.add_meta_graph_and_variables(
        sess, [tf.saved_model.tag_constants.SERVING],
        signature_def_map={
            tf.saved_model.signature_constants.DEFAULT_SERVING_SIGNATURE_DEF_KEY:
                signature_definition
        })
    # Save the model so we can serve it with a model server :)
    builder.save()

